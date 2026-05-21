package pl.quizpszczelarski.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Data ────────────────────────────────────────────────────────────────────

private const val PAIR_COUNT = 8
private const val GRID_COLS = 4

/** Pszczelarskie emoji pairs — each appears twice in the shuffled deck. */
private val BEE_EMOJIS = listOf("🐝", "🍯", "🌸", "👑", "🌻", "🦋", "🌿", "🐛")

private data class MemCard(
    val id: Int,
    val pairId: Int,
    val emoji: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false,
)

private fun buildDeck(): List<MemCard> =
    BEE_EMOJIS.flatMapIndexed { pairId, emoji ->
        listOf(
            MemCard(id = pairId * 2, pairId = pairId, emoji = emoji),
            MemCard(id = pairId * 2 + 1, pairId = pairId, emoji = emoji),
        )
    }.shuffled()

// ─── Board ───────────────────────────────────────────────────────────────────

@Composable
fun MemoryPairsBoard(
    onComplete: (score: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    var cards by remember { mutableStateOf(buildDeck()) }
    var firstIndex by remember { mutableStateOf<Int?>(null) }
    var isEvaluating by remember { mutableStateOf(false) }
    var attempts by remember { mutableStateOf(0) }
    var matchedPairs by remember { mutableStateOf(0) }

    fun onCardClick(index: Int) {
        if (isEvaluating) return
        val card = cards[index]
        if (card.isFlipped || card.isMatched) return

        val first = firstIndex

        if (first == null) {
            // Flip first card of the pair
            cards = cards.toMutableList().also { it[index] = it[index].copy(isFlipped = true) }
            firstIndex = index
        } else {
            // Flip second card and evaluate
            cards = cards.toMutableList().also { it[index] = it[index].copy(isFlipped = true) }
            attempts++
            isEvaluating = true
            firstIndex = null

            if (cards[first].pairId == cards[index].pairId) {
                // ✅ Match — mark both as matched
                cards = cards.toMutableList().also {
                    it[first] = it[first].copy(isFlipped = false, isMatched = true)
                    it[index] = it[index].copy(isFlipped = false, isMatched = true)
                }
                isEvaluating = false
                matchedPairs++

                if (matchedPairs == PAIR_COUNT) {
                    // All pairs found — compute score (100 at perfect play, −8 per extra attempt)
                    val score = maxOf(0, 100 - (attempts - PAIR_COUNT) * 8)
                    scope.launch {
                        delay(900)
                        onComplete(score)
                    }
                }
            } else {
                // ❌ No match — flip back after short pause
                scope.launch {
                    delay(800)
                    cards = cards.toMutableList().also {
                        it[first] = it[first].copy(isFlipped = false)
                        it[index] = it[index].copy(isFlipped = false)
                    }
                    isEvaluating = false
                }
            }
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
                text = "Pary pszczelarskie",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Pary: $matchedPairs / $PAIR_COUNT",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Znajdź wszystkie pasujące pary!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4×4 grid
        val rows = cards.chunked(GRID_COLS)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEachIndexed { rowIndex, rowCards ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowCards.forEachIndexed { colIndex, card ->
                        val index = rowIndex * GRID_COLS + colIndex
                        MemoryCardView(
                            card = card,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            onClick = { onCardClick(index) },
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Prób: $attempts",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ─── Card ────────────────────────────────────────────────────────────────────

@Composable
private fun MemoryCardView(
    card: MemCard,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isRevealed = card.isFlipped || card.isMatched

    val bgColor by animateColorAsState(
        targetValue = when {
            card.isMatched -> Color(0xFFAED581)
            isRevealed -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.tertiary
        },
        animationSpec = tween(220),
        label = "MemCardBg",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable(enabled = !isRevealed) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = isRevealed,
            transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(220)) },
            label = "MemCardReveal",
        ) { revealed ->
            Text(
                text = if (revealed) card.emoji else "🔶",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
