package pl.quizpszczelarski.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.quizpszczelarski.app.platform.LocalSettingsState
import pl.quizpszczelarski.app.presentation.home.HomeIntent
import pl.quizpszczelarski.app.presentation.home.HomeState
import pl.quizpszczelarski.app.ui.components.ActionCard
import pl.quizpszczelarski.app.ui.components.AppButton
import pl.quizpszczelarski.app.ui.components.AppButtonVariant
import pl.quizpszczelarski.app.ui.theme.AppTheme

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
                HomeMainContent(state, onIntent, uriHandler)
            } else {
                LevelSelectContent(state, onIntent)
            }
        }

        // Settings toggles — bottom-left corner (hidden during level selection)
        if (!state.showLevelSelect) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // Haptics toggle
            IconButton(
                onClick = { onIntent(HomeIntent.ToggleHaptics) },
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (settingsState.hapticsEnabled) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                ),
            ) {
                Text(
                    text = if (settingsState.hapticsEnabled) "📳" else "📴",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.alpha(if (settingsState.hapticsEnabled) 1f else 0.5f),
                )
            }

            // Sound toggle
            IconButton(
                onClick = { onIntent(HomeIntent.ToggleSound) },
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (settingsState.soundEnabled) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                ),
            ) {
                Text(
                    text = if (settingsState.soundEnabled) "🔊" else "🔇",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.alpha(if (settingsState.soundEnabled) 1f else 0.5f),
                )
            }
        }
        } // if (!state.showLevelSelect)
    } // Box
}

@Composable
private fun HomeMainContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    uriHandler: androidx.compose.ui.platform.UriHandler,
) {
    val spacing = AppTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.lg, vertical = spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header
        Text(
            text = "🐝",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
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

        // Action cards
        ActionCard(
            title = "Zagraj",
            description = "Rozpocznij quiz o pszczołach",
            icon = { Text("🎮", style = MaterialTheme.typography.headlineSmall) },
            onClick = { onIntent(HomeIntent.StartQuiz) },
            iconBackgroundColor = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(spacing.lg))

        ActionCard(
            title = "Ranking / statystyki",
            description = "Zobacz najlepsze wyniki",
            icon = { Text("🏆", style = MaterialTheme.typography.headlineSmall) },
            onClick = { onIntent(HomeIntent.ViewLeaderboard) },
            iconBackgroundColor = MaterialTheme.colorScheme.secondary,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Footer — coffee link
        Row(
            modifier = Modifier
                .clickable { uriHandler.openUri("https://buycoffee.to/codewithhoney") }
                .padding(spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("☕", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(spacing.sm))
            Text(
                text = "Postaw mi kawę",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
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
        // Top bar: back arrow left + centered title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.sm, vertical = spacing.md),
        ) {
            IconButton(
                onClick = { onIntent(HomeIntent.BackFromLevelSelect) },
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Text("←", style = MaterialTheme.typography.headlineSmall)
            }
            Text(
                text = "Wybierz poziom",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        // Centered content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AppButton(
                text = "🐝  Normalny",
                onClick = { onIntent(HomeIntent.SelectLevel("easy")) },
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
