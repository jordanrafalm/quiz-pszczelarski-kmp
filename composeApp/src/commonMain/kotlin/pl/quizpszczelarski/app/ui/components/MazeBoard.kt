package pl.quizpszczelarski.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

private const val MAZE_SIZE = 6

@Composable
fun MazeBoard(
    onComplete: (moves: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Seed from today so each day has a different maze
    val seed = remember {
        Clock.System.todayIn(TimeZone.currentSystemDefault()).dayOfYear.toLong()
    }
    val walls = remember(seed) { buildMazeWalls(seed) }

    var beePos by remember { mutableStateOf(0 to 0) }
    var moves by remember { mutableStateOf(0) }
    var completed by remember { mutableStateOf(false) }
    val visited = remember { mutableListOf(0 to 0).toMutableStateList() }

    val goalPos = (MAZE_SIZE - 1) to (MAZE_SIZE - 1)

    LaunchedEffect(beePos) {
        if (beePos == goalPos && !completed) {
            completed = true
            delay(600)
            onComplete(moves)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Pszczeli Labirynt",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Ruchów: $moves",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Poprowadź pszczołę do kwiatka!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 6×6 maze grid
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (row in 0 until MAZE_SIZE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    for (col in 0 until MAZE_SIZE) {
                        val pos = row to col
                        val isAdj = !walls.contains(pos) && mazeAdjacent(beePos, pos)

                        MazeCell(
                            isBee = beePos == pos,
                            isGoal = goalPos == pos,
                            isWall = walls.contains(pos),
                            isVisited = visited.contains(pos),
                            isAdjacent = isAdj && !completed,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            onClick = {
                                if (!completed && isAdj) {
                                    beePos = pos
                                    moves++
                                    if (!visited.contains(pos)) visited.add(pos)
                                }
                            },
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("🐝 Ty", style = MaterialTheme.typography.labelSmall)
            Text("🌸 Cel", style = MaterialTheme.typography.labelSmall)
            Text("▓ Ściana", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun MazeCell(
    isBee: Boolean,
    isGoal: Boolean,
    isWall: Boolean,
    isVisited: Boolean,
    isAdjacent: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isWall -> Color(0xFF4E342E)
            isBee -> Color(0xFFFFD600)
            isGoal -> Color(0xFF66BB6A)
            isVisited -> Color(0xFFFFF9C4)
            isAdjacent -> Color(0xFFFFE0B2)
            else -> Color(0xFFFFFDE7)
        },
        animationSpec = tween(200),
        label = "MazeCellBg",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(
                width = if (isAdjacent) 2.dp else 0.5.dp,
                color = if (isAdjacent) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(6.dp),
            )
            .clickable(enabled = !isWall, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isBee -> Text("🐝", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            isGoal -> Text("🌸", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            isWall -> Text("▓", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF3E2723), textAlign = TextAlign.Center)
        }
    }
}

/**
 * Checks if [to] is a direct (non-diagonal) neighbour of [from].
 */
private fun mazeAdjacent(from: Pair<Int, Int>, to: Pair<Int, Int>): Boolean {
    val dr = from.first - to.first
    val dc = from.second - to.second
    return (dr == 1 && dc == 0) || (dr == -1 && dc == 0) ||
        (dr == 0 && dc == 1) || (dr == 0 && dc == -1)
}

/**
 * Generates a set of wall positions for the maze deterministically based on [seed].
 *
 * Rules:
 * - Only interior cells (rows 1..4, cols 1..4) can become walls.
 * - Cells adjacent to start (0,0) or goal (5,5) are kept clear.
 * - ~30% density — ensures perimeter path from start to goal always exists.
 */
private fun buildMazeWalls(seed: Long): Set<Pair<Int, Int>> {
    val rng = kotlin.random.Random(seed)  // intentional: stdlib Random, not imported alias
    val goalPos = (MAZE_SIZE - 1) to (MAZE_SIZE - 1)
    val walls = mutableSetOf<Pair<Int, Int>>()

    for (row in 1 until MAZE_SIZE - 1) {
        for (col in 1 until MAZE_SIZE - 1) {
            val pos = row to col
            // Keep cells adjacent to start or goal clear
            if (mazeAdjacent(0 to 0, pos) || mazeAdjacent(goalPos, pos)) continue
            if (rng.nextFloat() < 0.30f) walls.add(pos)
        }
    }
    return walls
}
