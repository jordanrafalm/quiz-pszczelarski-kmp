package pl.quizpszczelarski.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import pl.quizpszczelarski.app.presentation.home.HomeIntent
import pl.quizpszczelarski.app.presentation.home.HomeState
import pl.quizpszczelarski.app.ui.components.ActionCard
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Home screen — header, two action cards (play + leaderboard), footer.
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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

        // Footer
        Text(
            text = "Każdy quiz składa się z 5 pytań",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
