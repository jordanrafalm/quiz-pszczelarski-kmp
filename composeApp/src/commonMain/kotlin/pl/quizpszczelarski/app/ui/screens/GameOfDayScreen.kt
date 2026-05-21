package pl.quizpszczelarski.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import pl.quizpszczelarski.app.navigation.AppTransitions
import pl.quizpszczelarski.app.presentation.gameofday.GameOfDayIntent
import pl.quizpszczelarski.app.presentation.gameofday.GameOfDayState
import pl.quizpszczelarski.app.ui.components.QuizTopBar
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Game of Day screen — shows Menu, then the selected game, then GameOver result.
 */
@Composable
fun GameOfDayScreen(
    state: GameOfDayState,
    onIntent: (GameOfDayIntent) -> Unit,
) {
    val spacing = AppTheme.spacing

    LaunchedEffect(Unit) {
        onIntent(GameOfDayIntent.LoadGameOfDay)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Top bar with title
        QuizTopBar(
            title = "Gra Dnia",
            onBackClick = { onIntent(GameOfDayIntent.BackToHome) },
        )

        // Main content area — animated based on screenState
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = state.screenState,
                transitionSpec = { AppTransitions.screenTransition() },
                label = "GameOfDayScreenState",
            ) { screenState ->
                when (screenState) {
                    GameOfDayState.ScreenState.Menu -> MenuContent(state, onIntent, spacing)
                    GameOfDayState.ScreenState.Playing -> PlayingContent(state, onIntent, spacing)
                    is GameOfDayState.ScreenState.GameOver -> GameOverContent(screenState, onIntent, spacing)
                }
            }
        }
    }
}

@Composable
private fun MenuContent(
    state: GameOfDayState,
    onIntent: (GameOfDayIntent) -> Unit,
    spacing: AppTheme.Spacing,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = state.todayType.emoji(),
            style = MaterialTheme.typography.displayLarge,
        )
        Spacer(modifier = Modifier.height(spacing.lg))
        Text(
            text = state.todayType.title(),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(spacing.md))
        Text(
            text = state.todayType.description(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.lg))

        // Show completion badge if already played today
        if (state.isCompleted) {
            Text(
                text = "✅ Grasz już dzisiaj! Wynik: ${state.score}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(spacing.lg))
        }

        Button(onClick = { onIntent(GameOfDayIntent.StartGame) }) {
            Text("Zagraj")
        }
    }
}

@Composable
private fun PlayingContent(
    state: GameOfDayState,
    onIntent: (GameOfDayIntent) -> Unit,
    spacing: AppTheme.Spacing,
) {
    // Placeholder for actual game canvas/components
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🎮 Gra: ${state.todayType.title()}",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(spacing.lg))
        Text(
            text = "Wynik: ${state.score}",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(spacing.lg))
        Text(
            text = "Implementacja w Commit 3-4...",
            style = MaterialTheme.typography.bodyMedium,
        )

        // Temporary end button for testing
        Spacer(modifier = Modifier.height(spacing.lg))
        Button(onClick = { onIntent(GameOfDayIntent.EndGame(42)) }) {
            Text("Koniec gry (test)")
        }
    }
}

@Composable
private fun GameOverContent(
    screenState: GameOfDayState.ScreenState.GameOver,
    onIntent: (GameOfDayIntent) -> Unit,
    spacing: AppTheme.Spacing,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge,
        )
        Spacer(modifier = Modifier.height(spacing.lg))
        Text(
            text = "Koniec gry",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(spacing.md))
        Text(
            text = "Twój wynik: ${screenState.score}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(spacing.lg))
        Button(onClick = { onIntent(GameOfDayIntent.RetryGame) }) {
            Text("Zagraj ponownie")
        }
        Spacer(modifier = Modifier.height(spacing.md))
        Button(onClick = { onIntent(GameOfDayIntent.BackToHome) }) {
            Text("Powrót do domu")
        }
    }
}
