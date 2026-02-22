package pl.quizpszczelarski.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

    // Animated colors for smooth transitions
    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(durationMillis = 250),
        label = "AnswerBorderColor",
    )
    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 250),
        label = "AnswerBgColor",
    )
    val animatedRadioColor by animateColorAsState(
        targetValue = radioColor,
        animationSpec = tween(durationMillis = 250),
        label = "AnswerRadioColor",
    )

    val contentAlpha = if (state == AnswerOptionState.Disabled) 0.5f else 1f

    // Press scale feedback
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "AnswerScale",
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && state != AnswerOptionState.Disabled,
            ) { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = animatedBackgroundColor),
        border = BorderStroke(2.dp, animatedBorderColor),
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
                color = animatedRadioColor,
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
