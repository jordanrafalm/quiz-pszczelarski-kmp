package pl.quizpszczelarski.app.ui.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Score display card for the result screen.
 *
 * Figma: Score display on ResultScreen.
 */
@Composable
fun ResultCard(
    score: Int,
    totalQuestions: Int,
    percentage: Int,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    // Animated count-up for score and percentage
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "ScoreCountUp",
    )
    val animatedPercentage by animateIntAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 800, delayMillis = 200, easing = EaseOutCubic),
        label = "PercentageCountUp",
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "$animatedScore/$totalQuestions",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            Text(
                text = "$animatedPercentage%",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = "poprawnych odpowiedzi",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
