package pl.quizpszczelarski.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * One-time intro dialog shown after an app update that introduces the Game of Day feature.
 * Shown only once, on the first launch after the feature appears.
 *
 * @param onDismiss Called when the user closes without playing (X button).
 * @param onPlayNow Called when the user taps "Zagraj" to go directly to Game of Day.
 */
@Composable
fun GameOfDayIntroDialog(
    onDismiss: () -> Unit,
    onPlayNow: () -> Unit,
) {
    val spacing = AppTheme.spacing

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Close button (top-right)
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Zamknij",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = "🎮",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(spacing.md))

            Text(
                text = "Nowy Tryb: Gra Dnia!",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(spacing.md))

            Text(
                text = "Codziennie nowa mini-gra z pszczelarskim twistem.\n\nMożesz zagrać 1 raz dziennie i zdobyć punkty do rankingu.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(spacing.xl))

            AppButton(
                text = "Zagraj teraz!",
                onClick = onPlayNow,
                leadingIcon = { Text("🐝") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
