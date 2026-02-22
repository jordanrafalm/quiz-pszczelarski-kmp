package pl.quizpszczelarski.app.ui.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Progress indicator showing current question number and percentage.
 *
 * Figma: QuizQuestionScreen progress section.
 *
 * @param currentQuestion 0-based question index.
 * @param totalQuestions Total number of questions in the quiz.
 */
@Composable
fun QuizProgressBar(
    currentQuestion: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val progress = if (totalQuestions == 0) 0f
    else (currentQuestion + 1).toFloat() / totalQuestions

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
        label = "ProgressAnimation",
    )
    val animatedPercentage = (animatedProgress * 100).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Pytanie ${currentQuestion + 1} / $totalQuestions",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$animatedPercentage%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(spacing.sm))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}
