package pl.quizpszczelarski.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Two-option (or N-option) tab selector.
 *
 * Figma: "All-time" / "Weekly" tabs on LeaderboardScreen.
 */
@Composable
fun TabSelector(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(spacing.xs),
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedIndex

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .then(
                        if (isSelected) {
                            Modifier.background(MaterialTheme.colorScheme.primary)
                        } else {
                            Modifier
                        },
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = spacing.md),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tab,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}
