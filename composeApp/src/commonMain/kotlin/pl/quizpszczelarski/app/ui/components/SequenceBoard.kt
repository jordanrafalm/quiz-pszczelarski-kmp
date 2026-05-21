package pl.quizpszczelarski.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

// ─── Constants ───────────────────────────────────────────────────────────────

/** Pszczelarskie emoji shown on the 2×3 symbol grid. */
private val SYMBOLS = listOf("🌸", "🌻", "🍯", "🐝", "🦋", "🌿")

private const val SEQ_START = 3    // sequence length in round 1
private const val MAX_ROUNDS = 6   // number of rounds (length grows to SEQ_START + MAX_ROUNDS - 1)
private const val SHOW_MS = 650L   // how long each symbol stays lit during Showing
private const val GAP_MS = 250L    // dark gap between symbols during Showing
private const val WRONG_MS = 1200L // delay before onComplete after a wrong tap
private const val WIN_MS = 900L    // delay before onComplete after finishing all rounds

// ─── State machine ───────────────────────────────────────────────────────────

private enum class SeqPhase { Showing, Inputting, Wrong, Finished }

// ─── Board ───────────────────────────────────────────────────────────────────

/**
 * Simon-Says style sequence game.
 *
 * The computer shows symbols one by one; the player must reproduce the same order.
 * Each round the sequence grows by one. Daily seed from [LocalDate.dayOfYear] ensures
 * every player sees the same sequence on the same day.
 *
 * @param onComplete Called with the accumulated score when the game ends.
 */
@Composable
fun SequenceBoard(
    onComplete: (score: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    // Deterministic master sequence based on today's date
    val seed = remember {
        Clock.System.todayIn(TimeZone.currentSystemDefault()).dayOfYear.toLong()
    }
    val masterSeq = remember(seed) {
        val rng = kotlin.random.Random(seed)
        List(SEQ_START + MAX_ROUNDS - 1) { rng.nextInt(SYMBOLS.size) }
    }

    var round by remember { mutableStateOf(1) }
    var phase by remember { mutableStateOf(SeqPhase.Showing) }
    var showingStep by remember { mutableStateOf<Int?>(null) }  // null = no symbol lit
    var userInput by remember { mutableStateOf(listOf<Int>()) }
    var score by remember { mutableStateOf(0) }
    var wrongIdx by remember { mutableStateOf<Int?>(null) }

    val seqLen = SEQ_START + round - 1
    val currentSeq = masterSeq.take(seqLen)

    // ── Showing animation: light up each symbol in sequence ──────────────────
    LaunchedEffect(round) {
        phase = SeqPhase.Showing
        showingStep = null
        userInput = emptyList()
        wrongIdx = null

        delay(600L) // brief pause before starting

        for (step in currentSeq.indices) {
            showingStep = step
            delay(SHOW_MS)
            showingStep = null
            delay(GAP_MS)
        }

        phase = SeqPhase.Inputting
    }

    // ── Tap handler ───────────────────────────────────────────────────────────
    fun onSymbolTap(symbolIdx: Int) {
        if (phase != SeqPhase.Inputting) return

        val step = userInput.size
        val expected = currentSeq[step]

        if (symbolIdx == expected) {
            val newInput = userInput + symbolIdx
            userInput = newInput

            if (newInput.size == seqLen) {
                // ✅ Round complete
                score += seqLen
                if (round >= MAX_ROUNDS) {
                    phase = SeqPhase.Finished
                    scope.launch { delay(WIN_MS); onComplete(score) }
                } else {
                    round++ // triggers LaunchedEffect(round) to restart animation
                }
            }
        } else {
            // ❌ Wrong symbol
            wrongIdx = symbolIdx
            phase = SeqPhase.Wrong
            scope.launch { delay(WRONG_MS); onComplete(score) }
        }
    }

    // ── UI ───────────────────────────────────────────────────────────────────
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Sekwencja kwiatów", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Runda: $round / $MAX_ROUNDS",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Phase label
        val phaseLabel = when (phase) {
            SeqPhase.Showing ->
                if (showingStep == null) "Gotuj się..." else "Zapamiętaj!"
            SeqPhase.Inputting ->
                "Twoja kolej! (${userInput.size} / $seqLen)"
            SeqPhase.Wrong -> "Błąd! 😔 Ale spróbuj jutro!"
            SeqPhase.Finished -> "Brawo! Ukończono wszystkie rundy 🎉"
        }
        Text(
            text = phaseLabel,
            style = MaterialTheme.typography.bodyLarge,
            color = when (phase) {
                SeqPhase.Wrong -> MaterialTheme.colorScheme.error
                SeqPhase.Finished -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress dots — one dot per step in current sequence
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            currentSeq.forEachIndexed { step, _ ->
                val isLit = showingStep == step
                val isDone = phase == SeqPhase.Inputting && step < userInput.size

                val dotColor by animateColorAsState(
                    targetValue = when {
                        isLit -> Color(0xFFFFD600)
                        isDone -> Color(0xFF66BB6A)
                        else -> Color(0xFFBDBDBD)
                    },
                    animationSpec = tween(150),
                    label = "DotColor$step",
                )
                val dotSize by animateFloatAsState(
                    targetValue = if (isLit) 18f else 12f,
                    animationSpec = tween(150),
                    label = "DotSize$step",
                )

                Box(
                    modifier = Modifier
                        .size(dotSize.dp)
                        .clip(CircleShape)
                        .background(dotColor),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2×3 symbol grid
        val symbolRows = SYMBOLS.chunked(3)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            symbolRows.forEachIndexed { rowIdx, rowEmojis ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowEmojis.forEachIndexed { colIdx, emoji ->
                        val symbolIdx = rowIdx * 3 + colIdx
                        val isLit = showingStep != null &&
                            currentSeq.getOrNull(showingStep!!) == symbolIdx
                        val isWrong = wrongIdx == symbolIdx

                        SymbolButton(
                            emoji = emoji,
                            isHighlighted = isLit,
                            isWrong = isWrong,
                            enabled = phase == SeqPhase.Inputting,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            onClick = { onSymbolTap(symbolIdx) },
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Wynik: $score pkt",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

// ─── Symbol button ────────────────────────────────────────────────────────────

@Composable
private fun SymbolButton(
    emoji: String,
    isHighlighted: Boolean,
    isWrong: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val btnScale by animateFloatAsState(
        targetValue = if (isHighlighted) 1.18f else 1.0f,
        animationSpec = tween(140),
        label = "SymScale",
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            isWrong -> Color(0xFFEF9A9A)           // red — wrong tap
            isHighlighted -> Color(0xFFFFD600)      // golden yellow — showing
            !enabled -> Color(0xFFE0E0E0)           // grey — locked during Showing
            else -> Color(0xFFFFF9C4)               // soft yellow — interactive
        },
        animationSpec = tween(140),
        label = "SymBg",
    )

    Box(
        modifier = modifier
            .scale(btnScale)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
    }
}
