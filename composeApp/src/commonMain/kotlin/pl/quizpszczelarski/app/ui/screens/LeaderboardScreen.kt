package pl.quizpszczelarski.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import pl.quizpszczelarski.app.presentation.leaderboard.LeaderboardIntent
import pl.quizpszczelarski.app.presentation.leaderboard.LeaderboardState
import pl.quizpszczelarski.app.ui.components.AppButton
import pl.quizpszczelarski.app.ui.components.AppButtonVariant
import pl.quizpszczelarski.app.ui.components.LeaderboardEntryRow
import pl.quizpszczelarski.app.ui.components.QuizTopBar
import pl.quizpszczelarski.app.ui.components.UserPositionCard
import pl.quizpszczelarski.app.ui.theme.AppTheme
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Leaderboard screen — top bar, tab selector, entry rows, user position card.
 *
 * Stateless composable: receives state, emits intents.
 */
@Composable
fun LeaderboardScreen(
    state: LeaderboardState,
    onIntent: (LeaderboardIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    // Handle back button
    BackHandler {
        onIntent(LeaderboardIntent.GoBack)
    }

    // Lottie composition for flying bee
    val beeComposition by rememberLottieComposition {
        val jsonBytes = quiz_pszczelarski_kmp.composeapp.generated.resources.Res.readBytes("files/honey_bee.json")
        LottieCompositionSpec.JsonString(jsonBytes.decodeToString())
    }

    // Animatable progress: 0f (left edge) → 1f (right edge)
    val flyProgress = remember { Animatable(0f) }
    // Random vertical offset (in dp) chosen fresh for each bee pass
    var beeRandomOffsetDp by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            // Pick a new random vertical position before each flight
            // Range: -200dp to +200dp relative to vertical center
            beeRandomOffsetDp = Random.nextFloat() * 400f - 200f
            // Reset to left
            flyProgress.snapTo(0f)
            // Fly across in 2.5 seconds
            flyProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2500, easing = LinearEasing),
            )
            // Pause before next flight
            delay(2500)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenWidthPx = constraints.maxWidth.toFloat()

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            // Top bar with back navigation
            QuizTopBar(
                title = "Ranking",
                onBackClick = { onIntent(LeaderboardIntent.GoBack) },
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                else -> {
                    // Leaderboard entries
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = spacing.lg),
                    ) {
                        items(
                            items = state.entries,
                            key = { "${it.uid}-${it.rank}" },
                        ) { entry ->
                            if (entry.isCurrentUser) {
                                // Current user row — tappable to edit nickname
                                Box(
                                    modifier = Modifier.clickable {
                                        onIntent(LeaderboardIntent.StartEditNickname)
                                    },
                                ) {
                                    LeaderboardEntryRow(
                                        rank = entry.rank,
                                        name = entry.name,
                                        score = entry.score,
                                        isCurrentUser = true,
                                    )
                                }

                                // Inline edit field
                                if (state.isEditingNickname) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = spacing.sm),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                        ),
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(spacing.md),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            OutlinedTextField(
                                                value = state.nicknameInput,
                                                onValueChange = {
                                                    onIntent(LeaderboardIntent.UpdateNicknameInput(it))
                                                },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f),
                                                label = { Text("Nowy nick") },
                                            )
                                            Spacer(modifier = Modifier.width(spacing.sm))
                                            AppButton(
                                                text = "OK",
                                                onClick = {
                                                    onIntent(LeaderboardIntent.ConfirmNickname)
                                                },
                                                enabled = state.nicknameInput.trim().isNotEmpty(),
                                                modifier = Modifier.width(80.dp),
                                            )
                                            Spacer(modifier = Modifier.width(spacing.sm))
                                            AppButton(
                                                text = "\u2715",
                                                onClick = {
                                                    onIntent(LeaderboardIntent.CancelEditNickname)
                                                },
                                                variant = AppButtonVariant.Secondary,
                                                modifier = Modifier.width(48.dp),
                                            )
                                        }
                                    }
                                }
                            } else {
                                LeaderboardEntryRow(
                                    rank = entry.rank,
                                    name = entry.name,
                                    score = entry.score,
                                    isCurrentUser = false,
                                )
                            }
                            Spacer(modifier = Modifier.height(spacing.sm))
                        }
                    }
                }
            }

            // User position card
            if (state.userRank > 0) {
                UserPositionCard(
                    rank = state.userRank,
                    score = state.userScore,
                    modifier = Modifier.padding(start = spacing.lg, end = spacing.lg, bottom = spacing.xxl, top = spacing.sm),
                )
            }
        } // end Column

        // Flying bee overlay — curved path (up-down-up) from left to right, random height each pass
        val beeSize = 48.dp
        val beeSizePx = with(LocalDensity.current) { beeSize.toPx() }
        val progress = flyProgress.value

        // Sine wave: 1.5 periods → up-down-up curve, centered on random base offset
        val waveAmplitudePx = with(LocalDensity.current) { 60.dp.toPx() }
        val waveOffset = (sin(progress.toDouble() * 2.0 * PI * 1.5) * waveAmplitudePx).toFloat()
        val totalOffsetY = beeRandomOffsetDp + with(LocalDensity.current) { waveOffset.toDp().value }

        // Only show while animating
        if (progress in 0.01f..0.99f) {
            Image(
                painter = rememberLottiePainter(
                    composition = beeComposition,
                    iterations = Compottie.IterateForever,
                ),
                contentDescription = "Flying bee",
                modifier = Modifier
                    .size(beeSize)
                    .align(Alignment.CenterStart)
                    .offset(
                        x = with(LocalDensity.current) {
                            val totalTravel = screenWidthPx + beeSizePx * 2
                            (-beeSizePx + progress * totalTravel).toDp()
                        },
                        y = totalOffsetY.dp,
                    ),
            )
        }
    } // end BoxWithConstraints
}
