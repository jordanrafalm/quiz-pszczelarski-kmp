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
import androidx.compose.material3.LinearProgressIndicator
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
import kotlin.random.Random

private const val BASE_MAZE_SIZE = 6
private const val TIME_LIMIT_SECONDS = 45
private const val BASE_MOVE_LIMIT = 22

/**
 * Enhanced Maze Board with difficulty scaling, time limits, moving enemies, and scoring.
 * Difficulty varies by day of week: Mon/Wed → easier, Tue/Thu/Fri/Sat/Sun → harder
 */
@Composable
fun MazeBoard(
    onComplete: (score: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val dayOfWeek = today.dayOfWeek.ordinal  // 0=Mon, 1=Tue, ..., 6=Sun
    
    // Difficulty: 0=easy, 1=medium, 2=hard
    val difficulty = when (dayOfWeek) {
        0, 2 -> 0  // Mon, Wed → easy
        1 -> 1     // Tue → medium
        else -> 2  // Thu, Fri, Sat, Sun → hard
    }
    
    val mazeSize = BASE_MAZE_SIZE + difficulty
    val moveLimit = BASE_MOVE_LIMIT + (difficulty * 4)
    val enemyCount = difficulty  // 0 easy, 1 medium enemy, 2 hard enemies
    
    val seed = today.dayOfYear.toLong()
    val walls = remember(seed, mazeSize) { buildMazeWalls(seed, mazeSize) }
    val goalPos = (mazeSize - 1) to (mazeSize - 1)

    var beePos by remember { mutableStateOf(0 to 0) }
    var moves by remember { mutableStateOf(0) }
    var completed by remember { mutableStateOf(false) }
    var timeLimitReached by remember { mutableStateOf(false) }
    var moveLimitReached by remember { mutableStateOf(false) }
    val visited = remember { mutableListOf(0 to 0).toMutableStateList() }
    
    // Enemy bees that move around
    var enemyPositions by remember { mutableStateOf(generateEnemyPositions(seed, mazeSize, enemyCount)) }
    var timeRemaining by remember { mutableStateOf(TIME_LIMIT_SECONDS) }

    // Check if player hit enemy
    LaunchedEffect(beePos, enemyPositions) {
        if (enemyPositions.contains(beePos) && !completed && !timeLimitReached) {
            // Hit enemy — restart
            beePos = 0 to 0
            visited.clear()
            visited.add(0 to 0)
            moves = 0
            delay(400)
            enemyPositions = generateEnemyPositions(seed, mazeSize, enemyCount)
        }
    }

    // Check if reached goal
    LaunchedEffect(beePos) {
        if (beePos == goalPos && !completed && !timeLimitReached) {
            completed = true
            delay(600)
            // Score = number of moves made (20 moves = 20 points)
            onComplete(moves)
        }
    }

    // Timer countdown
    LaunchedEffect(Unit) {
        repeat(TIME_LIMIT_SECONDS) {
            delay(1000)
            if (!completed) {
                timeRemaining--
                if (timeRemaining <= 0) {
                    timeLimitReached = true
                    completed = true
                    delay(400)
                    onComplete(0)  // Timeout = 0 score
                }
            }
        }
    }

    // Enemy movement every 2 seconds
    LaunchedEffect(enemyCount) {
        if (enemyCount > 0) {
            while (!completed && !timeLimitReached) {
                delay(2000)
                enemyPositions = moveEnemies(enemyPositions, seed, mazeSize, walls, goalPos)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header with title and stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Pszczeli Labirynt (Trudność: ${getDifficultyLabel(difficulty)})",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Ruchów: $moves/$moveLimit",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (moves >= moveLimit * 0.8) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "⏱ ${timeRemaining}s",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (timeRemaining <= 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
                LinearProgressIndicator(
                    progress = { timeRemaining.toFloat() / TIME_LIMIT_SECONDS },
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(4.dp),
                    color = if (timeRemaining <= 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val statusText = when {
            timeLimitReached -> "⏰ Czas się skończył!"
            moves >= moveLimit -> "❌ Przekroczono limit ruchów!"
            else -> "Poprowadź pszczołę do kwiatka (unikaj wrogów 🐝❌)"
        }
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodySmall,
            color = if (timeLimitReached || moves >= moveLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Maze grid
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            for (row in 0 until mazeSize) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    for (col in 0 until mazeSize) {
                        val pos = row to col
                        val isAdjacent = !walls.contains(pos) && mazeAdjacent(beePos, pos) && !enemyPositions.contains(pos)
                        val isEnemy = enemyPositions.contains(pos)

                        MazeCell(
                            isBee = beePos == pos,
                            isGoal = goalPos == pos,
                            isWall = walls.contains(pos),
                            isVisited = visited.contains(pos),
                            isAdjacent = isAdjacent && !completed && !timeLimitReached && moves < moveLimit,
                            isEnemy = isEnemy && !completed,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            onClick = {
                                if (!completed && !timeLimitReached && moves < moveLimit && isAdjacent) {
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

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("🐝 Ty", style = MaterialTheme.typography.labelSmall)
            Text("🌸 Cel", style = MaterialTheme.typography.labelSmall)
            Text("▓ Ściana", style = MaterialTheme.typography.labelSmall)
            if (enemyCount > 0) Text("🐝❌ Wróg", style = MaterialTheme.typography.labelSmall)
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
    isEnemy: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isWall -> Color(0xFF4E342E)
            isBee -> Color(0xFFFFD600)
            isGoal -> Color(0xFF66BB6A)
            isEnemy -> Color(0xFFEF5350)
            isVisited -> Color(0xFFFFF9C4)
            isAdjacent -> Color(0xFFFFE0B2)
            else -> Color(0xFFFFFDE7)
        },
        animationSpec = tween(200),
        label = "MazeCellBg",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(bgColor)
            .border(
                width = if (isAdjacent || isEnemy) 2.dp else 0.5.dp,
                color = when {
                    isEnemy -> Color(0xFFC62828)
                    isAdjacent -> MaterialTheme.colorScheme.primary
                    else -> Color(0xFFE0E0E0)
                },
                shape = RoundedCornerShape(5.dp),
            )
            .clickable(enabled = !isWall && isAdjacent, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isBee -> Text("🐝", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            isGoal -> Text("🌸", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            isEnemy -> Text("❌", style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
            isWall -> Text("▓", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF3E2723), textAlign = TextAlign.Center)
        }
    }
}

private fun getDifficultyLabel(difficulty: Int) = when (difficulty) {
    0 -> "🟢 Łatwy"
    1 -> "🟡 Średni"
    else -> "🔴 Trudny"
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
 * Density increases with maze size.
 */
private fun buildMazeWalls(seed: Long, mazeSize: Int): Set<Pair<Int, Int>> {
    val rng = Random(seed)
    val goalPos = (mazeSize - 1) to (mazeSize - 1)
    val walls = mutableSetOf<Pair<Int, Int>>()

    // Density increases with size
    val density = 0.25f + (mazeSize - BASE_MAZE_SIZE) * 0.05f
    
    for (row in 1 until mazeSize - 1) {
        for (col in 1 until mazeSize - 1) {
            val pos = row to col
            // Keep cells adjacent to start or goal clear
            if (mazeAdjacent(0 to 0, pos) || mazeAdjacent(goalPos, pos)) continue
            if (rng.nextFloat() < density) walls.add(pos)
        }
    }
    return walls
}

private fun generateEnemyPositions(seed: Long, mazeSize: Int, count: Int): List<Pair<Int, Int>> {
    val rng = Random(seed + 999)  // Different seed for enemies
    val positions = mutableListOf<Pair<Int, Int>>()
    val attempts = 100
    var attemptsLeft = attempts
    
    while (positions.size < count && attemptsLeft > 0) {
        val pos = rng.nextInt(mazeSize) to rng.nextInt(mazeSize)
        if (pos != (0 to 0) && pos != (mazeSize - 1 to mazeSize - 1) && !positions.contains(pos)) {
            positions.add(pos)
        }
        attemptsLeft--
    }
    
    return positions
}

private fun moveEnemies(
    enemies: List<Pair<Int, Int>>,
    seed: Long,
    mazeSize: Int,
    walls: Set<Pair<Int, Int>>,
    goal: Pair<Int, Int>,
): List<Pair<Int, Int>> {
    val rng = Random(seed + 998)  // Deterministic per-day movement
    
    return enemies.map { enemy ->
        // Simple AI: try to move closer to goal, with randomness
        val neighbors = listOf(
            enemy.first + 1 to enemy.second,
            enemy.first - 1 to enemy.second,
            enemy.first to enemy.second + 1,
            enemy.first to enemy.second - 1,
        ).filter { (r, c) ->
            r in 0 until mazeSize && c in 0 until mazeSize && !walls.contains((r to c))
        }
        
        if (neighbors.isEmpty()) enemy
        else {
            // 70% chance to move toward goal, 30% random
            if (rng.nextFloat() < 0.7) {
                neighbors.minByOrNull { 
                    val dr = it.first - goal.first
                    val dc = it.second - goal.second
                    dr * dr + dc * dc
                } ?: enemy
            } else {
                neighbors[rng.nextInt(neighbors.size)]
            }
        }
    }
}
