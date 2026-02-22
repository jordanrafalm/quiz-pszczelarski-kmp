package pl.quizpszczelarski.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import pl.quizpszczelarski.app.platform.LocalSettingsState
import pl.quizpszczelarski.app.platform.LocalSoundPlayer
import pl.quizpszczelarski.app.presentation.result.ResultIntent
import pl.quizpszczelarski.app.presentation.result.ResultState
import pl.quizpszczelarski.app.ui.components.AppButton
import pl.quizpszczelarski.app.ui.components.AppButtonVariant
import pl.quizpszczelarski.app.ui.components.ResultCard
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Result screen — score icon, message, ResultCard, action buttons.
 *
 * Stateless composable: receives state, emits intents.
 */
@Composable
fun ResultScreen(
    state: ResultState,
    onIntent: (ResultIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    // Lottie composition for background bee animation
    val beeComposition by rememberLottieComposition {
        val jsonBytes = quiz_pszczelarski_kmp.composeapp.generated.resources.Res.readBytes("files/honey_bee_flying.json")
        LottieCompositionSpec.JsonString(jsonBytes.decodeToString())
    }

    // Staggered entry animation
    var showResult by remember { mutableStateOf(false) }
    
    // Play result sound if enabled
    val soundPlayer = LocalSoundPlayer.current
    val settingsState = LocalSettingsState.current
    LaunchedEffect(Unit) {
        if (settingsState.soundEnabled) {
            soundPlayer?.let { player ->
                val soundBytes = quiz_pszczelarski_kmp.composeapp.generated.resources.Res.readBytes("files/result_sound.mp3")
                player.play(soundBytes)
            }
        }
        delay(150)
        showResult = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Background bee animation — top area, subtle
        Image(
            painter = rememberLottiePainter(
                composition = beeComposition,
                iterations = Compottie.IterateForever,
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .align(Alignment.TopCenter)
                .alpha(0.6f),
        )

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Top bar with close button — X on the right, no divider (same as QuizScreen)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg, vertical = spacing.sm),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { onIntent(ResultIntent.GoBack) },
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✕",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = spacing.lg, vertical = spacing.xl)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Trophy / award icon
                AnimatedVisibility(
                    visible = showResult,
                    enter = fadeIn(tween(400)) + scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(400, easing = EaseOutCubic),
                    ),
                ) {
                    Text(
                        text = if (state.isHighScore) "🏆" else "🎯",
                        style = MaterialTheme.typography.displayLarge,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xl))

                // Result message
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(spacing.sm))

                Text(
                    text = "Zakończyłeś quiz",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(spacing.xxl))

                // Score card with entry animation
                AnimatedVisibility(
                    visible = showResult,
                    enter = fadeIn(tween(500, delayMillis = 100)) + slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(500, delayMillis = 100, easing = EaseOutCubic),
                    ),
                ) {
                    ResultCard(
                        score = state.score,
                        totalQuestions = state.totalQuestions,
                        percentage = state.percentage,
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xxl))

                // Play again button
                AppButton(
                    text = "Zagraj ponownie",
                    onClick = { onIntent(ResultIntent.PlayAgain) },
                    leadingIcon = { Text("🔄") },
                )

                Spacer(modifier = Modifier.height(spacing.md))

                // Leaderboard button
                AppButton(
                    text = "Zobacz ranking",
                    onClick = { onIntent(ResultIntent.ViewLeaderboard) },
                    variant = AppButtonVariant.Secondary,
                    leadingIcon = { Text("🏆") },
                )

                // Nickname prompt (shown when user hasn't set a custom nickname)
                if (state.showNicknamePrompt) {
                    Spacer(modifier = Modifier.height(spacing.lg))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    ) {
                        Column(modifier = Modifier.padding(spacing.lg)) {
                            Text(
                                text = "Ustaw swój nick",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Spacer(modifier = Modifier.height(spacing.md))

                            OutlinedTextField(
                                value = state.nicknameInput,
                                onValueChange = { onIntent(ResultIntent.UpdateNicknameInput(it)) },
                                label = { Text("Nick") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                            )

                            Spacer(modifier = Modifier.height(spacing.md))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                AppButton(
                                    text = "Zapisz",
                                    onClick = { onIntent(ResultIntent.ConfirmNickname) },
                                    enabled = state.nicknameInput.trim().isNotEmpty(),
                                    modifier = Modifier.weight(1f),
                                )
                                AppButton(
                                    text = "Pomiń",
                                    onClick = { onIntent(ResultIntent.DismissNicknameDialog) },
                                    variant = AppButtonVariant.Secondary,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            } // Column
        } // inner Column
    } // Box
}
