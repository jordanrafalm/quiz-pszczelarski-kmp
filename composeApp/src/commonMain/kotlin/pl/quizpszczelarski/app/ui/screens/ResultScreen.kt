package pl.quizpszczelarski.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = spacing.lg, vertical = spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Trophy / award icon
        Text(
            text = if (state.isHighScore) "🏆" else "🎯",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
        )

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

        // Score card
        ResultCard(
            score = state.score,
            totalQuestions = state.totalQuestions,
            percentage = state.percentage,
        )

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
    }
}
