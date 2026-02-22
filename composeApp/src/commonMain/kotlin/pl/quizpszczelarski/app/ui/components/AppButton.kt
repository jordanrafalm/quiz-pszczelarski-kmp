package pl.quizpszczelarski.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Button variants matching Figma design.
 */
enum class AppButtonVariant { Primary, Secondary, Tertiary }

/**
 * Shared button component.
 *
 * Figma: "Dalej" (QuizScreen), "Zagraj ponownie" (ResultScreen), "Zobacz ranking" (ResultScreen)
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val spacing = AppTheme.spacing

    when (variant) {
        AppButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                contentPadding = PaddingValues(
                    horizontal = spacing.xl,
                    vertical = spacing.md,
                ),
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }

        AppButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                contentPadding = PaddingValues(
                    horizontal = spacing.xl,
                    vertical = spacing.md,
                ),
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }

        AppButtonVariant.Tertiary -> {
            TextButton(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                contentPadding = PaddingValues(
                    horizontal = spacing.xl,
                    vertical = spacing.md,
                ),
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    leadingIcon: @Composable (() -> Unit)?,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
