package pl.quizpszczelarski.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Single leaderboard entry row.
 *
 * Figma: Leaderboard entries on LeaderboardScreen.
 *
 * @param rank Position in the leaderboard (1-based).
 * @param name Player display name.
 * @param score Player score.
 * @param isCurrentUser Whether to highlight as current user.
 */
@Composable
fun LeaderboardEntryRow(
    rank: Int,
    name: String,
    score: Int,
    isCurrentUser: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    val containerColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val border = if (isCurrentUser) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        null
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = border,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Rank circle
            RankBadge(rank = rank)

            Spacer(modifier = Modifier.width(spacing.md))

            // Name
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = if (isCurrentUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(spacing.sm))

            // Score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "punktów",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RankBadge(rank: Int) {
    val rankIcon = when (rank) {
        1 -> "🏆"
        2 -> "🥈"
        3 -> "🥉"
        else -> null
    }

    val bgColor = when (rank) {
        1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        2 -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
        3 -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center,
    ) {
        if (rankIcon != null) {
            Text(text = rankIcon, style = MaterialTheme.typography.bodyLarge)
        } else {
            Text(
                text = "$rank",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
