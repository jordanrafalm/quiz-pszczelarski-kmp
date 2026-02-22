package pl.quizpszczelarski.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * States for the answer option component.
 */
enum class AnswerOptionState { Default, Selected, Correct, Wrong, Disabled }

/**
 * Answer selection component for quiz questions.
 *
 * Figma: Answer options in QuizQuestionScreen.
 */
@Composable
fun AnswerOption(
    text: String,
    state: AnswerOptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val spacing = AppTheme.spacing
    val extendedColors = AppTheme.extendedColors

    val borderColor = when (state) {
        AnswerOptionState.Default -> MaterialTheme.colorScheme.outline
        AnswerOptionState.Selected -> extendedColors.selectedAnswerBorder
        AnswerOptionState.Correct -> extendedColors.correctAnswer
        AnswerOptionState.Wrong -> extendedColors.wrongAnswer
        AnswerOptionState.Disabled -> MaterialTheme.colorScheme.outline
    }

    val backgroundColor = when (state) {
        AnswerOptionState.Default -> MaterialTheme.colorScheme.surface
        AnswerOptionState.Selected -> extendedColors.selectedAnswerBackground
        AnswerOptionState.Correct -> extendedColors.correctAnswer.copy(alpha = 0.1f)
        AnswerOptionState.Wrong -> extendedColors.wrongAnswer.copy(alpha = 0.1f)
        AnswerOptionState.Disabled -> MaterialTheme.colorScheme.surface
    }

    val radioColor = when (state) {
        AnswerOptionState.Selected -> extendedColors.selectedAnswerBorder
        AnswerOptionState.Correct -> extendedColors.correctAnswer
        AnswerOptionState.Wrong -> extendedColors.wrongAnswer
        else -> MaterialTheme.colorScheme.outline
    }

    val contentAlpha = if (state == AnswerOptionState.Disabled) 0.5f else 1f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && state != AnswerOptionState.Disabled) { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, borderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Radio circle indicator
            RadioCircle(
                filled = state == AnswerOptionState.Selected ||
                    state == AnswerOptionState.Correct ||
                    state == AnswerOptionState.Wrong,
                color = radioColor,
                alpha = contentAlpha,
            )
            Spacer(modifier = Modifier.width(spacing.md))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
            )
        }
    }
}

@Composable
private fun RadioCircle(
    filled: Boolean,
    color: Color,
    alpha: Float,
    modifier: Modifier = Modifier,
) {
    val circleSize = 20.dp
    val innerSize = 10.dp

    if (filled) {
        Box(
            modifier = modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha * 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(innerSize)
                    .clip(CircleShape)
                    .background(color.copy(alpha = alpha)),
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha * 0.3f)),
        )
    }
}
