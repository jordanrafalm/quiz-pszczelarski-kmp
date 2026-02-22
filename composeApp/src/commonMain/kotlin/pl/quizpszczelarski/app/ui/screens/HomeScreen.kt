package pl.quizpszczelarski.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import pl.quizpszczelarski.app.platform.LocalSettingsState
import pl.quizpszczelarski.app.presentation.home.HomeIntent
import pl.quizpszczelarski.app.presentation.home.HomeState
import pl.quizpszczelarski.app.ui.components.ActionCard
import pl.quizpszczelarski.app.ui.components.QuizTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.quizpszczelarski.app.ui.components.AppButton
import pl.quizpszczelarski.app.ui.components.AppButtonVariant
import pl.quizpszczelarski.app.ui.theme.AppTheme
import kotlin.math.PI
import kotlin.math.sin

/**
 * Home screen — header, two action cards (play + leaderboard), footer.
 * Includes level selection sub-screen with animated transition.
 *
 * Stateless composable: receives state, emits intents.
 */
@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val settingsState = LocalSettingsState.current
    val uriHandler = LocalUriHandler.current

    // Handle back button when level select is shown
    BackHandler(enabled = state.showLevelSelect) {
        onIntent(HomeIntent.BackFromLevelSelect)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AnimatedContent(
            targetState = state.showLevelSelect,
            transitionSpec = {
                if (targetState) {
                    fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(300),
                    ) togetherWith fadeOut(tween(200)) + slideOutVertically(
                        targetOffsetY = { -it / 4 },
                        animationSpec = tween(200),
                    )
                } else {
                    fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { -it / 4 },
                        animationSpec = tween(300),
                    ) togetherWith fadeOut(tween(200)) + slideOutVertically(
                        targetOffsetY = { it / 4 },
                        animationSpec = tween(200),
                    )
                }
            },
            label = "HomeLevelToggle",
        ) { showLevel ->
            if (!showLevel) {
                HomeMainContent(state, onIntent)
            } else {
                LevelSelectContent(state, onIntent)
            }
        }

        // Settings toggles and coffee — bottom bar (hidden during level selection)
        if (!state.showLevelSelect) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg, vertical = spacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Settings toggles
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Haptics toggle
                    IconButton(
                        onClick = { onIntent(HomeIntent.ToggleHaptics) },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (settingsState.hapticsEnabled) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                        ),
                    ) {
                        Text(
                            text = "≋",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.alpha(if (settingsState.hapticsEnabled) 1f else 0.4f),
                        )
                    }

                    // Sound toggle
                    IconButton(
                        onClick = { onIntent(HomeIntent.ToggleSound) },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (settingsState.soundEnabled) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                        ),
                    ) {
                        Text(
                            text = "♪",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.alpha(if (settingsState.soundEnabled) 1f else 0.4f),
                        )
                    }
                }

                // Coffee link
                val coffeeScale = remember { Animatable(1f) }
                val coffeeOffsetY = remember { Animatable(0f) }
                val scope = rememberCoroutineScope()
                Row(
                    modifier = Modifier
                        .clickable {
                            scope.launch {
                                val scaleJob = launch {
                                    coffeeScale.animateTo(6f, tween(durationMillis = 1000))
                                }
                                val offsetJob = launch {
                                    coffeeOffsetY.animateTo(-60f, tween(durationMillis = 1000))
                                }
                                scaleJob.join()
                                offsetJob.join()
                                uriHandler.openUri("https://buycoffee.to/codewithhoney")
                                coffeeScale.snapTo(1f)
                                coffeeOffsetY.snapTo(0f)
                            }
                        }
                        .padding(spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "☕",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .scale(coffeeScale.value)
                            .offset(y = coffeeOffsetY.value.dp),
                    )
                    Spacer(modifier = Modifier.width(spacing.xs))
                    Text(
                        text = "Postaw mi kawę",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
    } // Box
}

@Composable
private fun HomeMainContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
) {
    val spacing = AppTheme.spacing

    // Easter egg: tap bee 5 times to trigger full animation sequence
    var beeTapCount by remember { mutableStateOf(0) }
    var easterEggActive by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Animation values
    val buttonsAlpha = remember { Animatable(1f) }
    val beeOffsetX = remember { Animatable(0f) }  // 0=home, 1=off-right, -1=off-left
    val beeVisible = remember { Animatable(1f) }
    val bearAlpha = remember { Animatable(0f) }
    val bearScale = remember { Animatable(0.5f) }

    // Lottie compositions
    val beeComposition by rememberLottieComposition {
        val jsonBytes = quiz_pszczelarski_kmp.composeapp.generated.resources.Res.readBytes("files/loading_flying_bee.json")
        LottieCompositionSpec.JsonString(jsonBytes.decodeToString())
    }
    val bearComposition by rememberLottieComposition {
        val jsonBytes = quiz_pszczelarski_kmp.composeapp.generated.resources.Res.readBytes("files/brown_bear.json")
        LottieCompositionSpec.JsonString(jsonBytes.decodeToString())
    }

    // Bee wave (sine curve during flight)
    val beeProgress = beeOffsetX.value
    val beeWaveY = (sin(beeProgress.toDouble() * 2.0 * PI * 1.5) * 50.0).toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.lg, vertical = spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Bee with flight animation
        Image(
            painter = rememberLottiePainter(
                composition = beeComposition,
                iterations = Compottie.IterateForever,
            ),
            contentDescription = "Bee animation",
            modifier = Modifier
                .size(120.dp)
                .alpha(beeVisible.value)
                .offset(
                    x = (beeProgress * 500f).dp,
                    y = beeWaveY.dp,
                )
                .clickable(enabled = !easterEggActive) {
                    beeTapCount++
                    if (beeTapCount >= 5) {
                        beeTapCount = 0
                        easterEggActive = true
                        scope.launch {
                            // 1. Buttons fade out
                            buttonsAlpha.animateTo(0f, tween(400))
                            // 2. Bee flies right along sine curve
                            beeOffsetX.animateTo(1f, tween(1000))
                            // Hide bee, reset position
                            beeVisible.snapTo(0f)
                            beeOffsetX.snapTo(0f)
                            // 3. Bear fades in with scale
                            launch { bearAlpha.animateTo(1f, tween(600)) }
                            bearScale.animateTo(1f, tween(600))
                            // 4. Bear stays visible
                            delay(2500)
                            // 5. Bear fades out
                            launch { bearAlpha.animateTo(0f, tween(600)) }
                            bearScale.animateTo(0.5f, tween(600))
                            // 6. Bee enters from left along sine curve
                            beeOffsetX.snapTo(-1f)
                            beeVisible.snapTo(1f)
                            beeOffsetX.animateTo(0f, tween(1000))
                            // 7. Buttons fade in
                            buttonsAlpha.animateTo(1f, tween(400))
                            easterEggActive = false
                        }
                    }
                },
        )

        Spacer(modifier = Modifier.height(spacing.lg))

        Text(
            text = state.appTitle,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        Text(
            text = state.appDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(spacing.xxl))

        // Bear easter egg — fades in/out during animation
        if (bearAlpha.value > 0.01f) {
            Image(
                painter = rememberLottiePainter(
                    composition = bearComposition,
                    iterations = Compottie.IterateForever,
                ),
                contentDescription = "Brown Bear",
                modifier = Modifier
                    .size(200.dp)
                    .alpha(bearAlpha.value)
                    .scale(bearScale.value),
            )
        }

        // Action cards — fade with animated alpha
        Column(
            modifier = Modifier.alpha(buttonsAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ActionCard(
                title = "Zagraj",
                description = "Rozpocznij quiz o pszczołach",
                icon = { Text("🎮", style = MaterialTheme.typography.headlineSmall) },
                onClick = { if (!easterEggActive) onIntent(HomeIntent.StartQuiz) },
                iconBackgroundColor = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            ActionCard(
                title = "Ranking",
                description = "Zobacz najlepsze wyniki",
                icon = { Text("🏆", style = MaterialTheme.typography.headlineSmall) },
                onClick = { if (!easterEggActive) onIntent(HomeIntent.ViewLeaderboard) },
                iconBackgroundColor = MaterialTheme.colorScheme.secondary,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom spacing for the bottom bar
        Spacer(modifier = Modifier.height(spacing.xl))
    }
}

@Composable
private fun LevelSelectContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
) {
    val spacing = AppTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Top bar with separator
        QuizTopBar(
            title = "Wybierz poziom",
            onBackClick = { onIntent(HomeIntent.BackFromLevelSelect) },
        )

        // Centered content with scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AppButton(
                text = "🐝  Normalny",
                onClick = { onIntent(HomeIntent.SelectLevel("easy")) },
                variant = AppButtonVariant.Secondary,
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            AppButton(
                text = "🏆  Pro dla Pszczelarzy",
                onClick = { onIntent(HomeIntent.SelectLevel("pro")) },
                variant = AppButtonVariant.Secondary,
            )

            Spacer(modifier = Modifier.height(spacing.xxl))

            // Question count selector
            Text(
                text = "Liczba pytań",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(spacing.md))

            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                listOf(5, 10, 15, 20).forEach { count ->
                    val isSelected = count == state.selectedQuestionCount
                    if (isSelected) {
                        Button(
                            onClick = { onIntent(HomeIntent.SetQuestionCount(count)) },
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        ) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onIntent(HomeIntent.SetQuestionCount(count)) },
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        ) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}