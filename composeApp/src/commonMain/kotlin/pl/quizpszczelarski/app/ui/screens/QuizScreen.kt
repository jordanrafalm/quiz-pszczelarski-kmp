package pl.quizpszczelarski.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.quizpszczelarski.app.presentation.quiz.QuizIntent
import pl.quizpszczelarski.app.presentation.quiz.QuizState
import pl.quizpszczelarski.app.ui.components.AnswerOption
import pl.quizpszczelarski.app.ui.components.AnswerOptionState
import pl.quizpszczelarski.app.ui.components.AppButton
import pl.quizpszczelarski.app.ui.components.QuizProgressBar
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Quiz screen — progress bar, question card, answer options, next button.
 *
 * Stateless composable: receives state, emits intents.
 */
@Composable
fun QuizScreen(
    state: QuizState,
    onIntent: (QuizIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val currentQuestion = state.currentQuestion ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Spacer(modifier = Modifier.height(spacing.lg))

        // Progress bar
        QuizProgressBar(
            currentQuestion = state.currentQuestionIndex,
            totalQuestions = state.totalQuestions,
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        // Question card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Text(
                text = currentQuestion.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(spacing.xl),
            )
        }

        Spacer(modifier = Modifier.height(spacing.xl))

        // Answer options
        Column(
            modifier = Modifier.padding(horizontal = spacing.lg),
        ) {
            currentQuestion.options.forEachIndexed { index, option ->
                AnswerOption(
                    text = option,
                    state = when {
                        state.selectedAnswerIndex == index -> AnswerOptionState.Selected
                        else -> AnswerOptionState.Default
                    },
                    onClick = { onIntent(QuizIntent.SelectAnswer(index)) },
                )
                if (index < currentQuestion.options.lastIndex) {
                    Spacer(modifier = Modifier.height(spacing.sm))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Next / Finish button
        AppButton(
            text = if (state.isLastQuestion) "Zakończ quiz" else "Dalej",
            onClick = { onIntent(QuizIntent.NextQuestion) },
            enabled = state.canProceed,
            modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.lg),
        )
    }
}
