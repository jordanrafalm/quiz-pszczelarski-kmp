package pl.quizpszczelarski.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import pl.quizpszczelarski.app.navigation.AppTransitions
import pl.quizpszczelarski.app.presentation.gameofday.GameOfDayIntent
import pl.quizpszczelarski.app.presentation.gameofday.GameOfDayState
import pl.quizpszczelarski.app.presentation.gameofday.GameOfDayType
import pl.quizpszczelarski.app.ui.components.FlappyBeeView
import pl.quizpszczelarski.app.ui.components.MazeBoard
import pl.quizpszczelarski.app.ui.components.MemoryPairsBoard
import pl.quizpszczelarski.app.ui.components.SequenceBoard
import pl.quizpszczelarski.app.ui.components.QuizTopBar
import pl.quizpszczelarski.app.ui.theme.AppTheme
import pl.quizpszczelarski.app.ui.theme.AppSpacing

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

        // Main content — no outer padding so FlappyBee can fill the screen edge-to-edge
        AnimatedContent(
            targetState = state.screenState,
            transitionSpec = { AppTransitions.screenTransition() },
            modifier = Modifier.fillMaxSize(),
            label = "GameOfDayScreenState",
        ) { screenState ->
            when (screenState) {
                GameOfDayState.ScreenState.Menu -> MenuContent(state, onIntent, spacing)
                GameOfDayState.ScreenState.Playing -> PlayingContent(state, onIntent, spacing)
                is GameOfDayState.ScreenState.GameOver -> GameOverContent(screenState, state.todayType, onIntent, spacing)
            }
        }
    }
}

@Composable
private fun MenuContent(
    state: GameOfDayState,
    onIntent: (GameOfDayIntent) -> Unit,
    spacing: AppSpacing,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.lg),
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

        // Game type selector — lets the user switch games for testing
        Text(
            text = "Wybierz grę:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GameOfDayType.entries.chunked(2).forEach { rowTypes ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowTypes.forEach { type ->
                        FilterChip(
                            selected = state.todayType == type,
                            onClick = { onIntent(GameOfDayIntent.SelectGameType(type)) },
                            label = { Text("${type.emoji()} ${type.title()}") },
                        )
                    }
                }
            }
        }

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
    spacing: AppSpacing,
) {
    when (state.todayType) {
        GameOfDayType.FlappyBee -> FlappyBeeView(
            onGameOver = { score -> onIntent(GameOfDayIntent.EndGame(score)) },
            modifier = Modifier.fillMaxSize(),
        )
        GameOfDayType.Maze -> MazeBoard(
            onComplete = { moves -> onIntent(GameOfDayIntent.EndGame(moves)) },
            modifier = Modifier.fillMaxSize(),
        )
        GameOfDayType.Memory -> MemoryPairsBoard(
            onComplete = { score -> onIntent(GameOfDayIntent.EndGame(score)) },
            modifier = Modifier.fillMaxSize(),
        )
        GameOfDayType.Sequence -> SequenceBoard(
            onComplete = { score -> onIntent(GameOfDayIntent.EndGame(score)) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun GameOverContent(
    screenState: GameOfDayState.ScreenState.GameOver,
    gameType: GameOfDayType,
    onIntent: (GameOfDayIntent) -> Unit,
    spacing: AppSpacing,
) {
    val scoreLabel = when (gameType) {
        GameOfDayType.Maze -> "Liczba ruchów: ${screenState.score} 🐝"
        GameOfDayType.Memory -> "Wynik: ${screenState.score} / 100 pkt"
        GameOfDayType.FlappyBee -> "Wynik: ${screenState.score} przejść"
        GameOfDayType.Sequence -> "Wynik: ${screenState.score} pkt"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge,
        )
        Spacer(modifier = Modifier.height(spacing.lg))
        Text(
            text = "Dobra robota!",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(spacing.md))
        Text(
            text = scoreLabel,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(spacing.md))
        
        // Ranking info — conditional on whether points were counted
        if (screenState.pointsAdded) {
            Text(
                text = "✅ Punkty dodane do rankingu!",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = "ℹ️ Już grałeś dzisiaj — wynik nie liczył się ponownie",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
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
