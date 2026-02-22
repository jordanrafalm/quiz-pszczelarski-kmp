package pl.quizpszczelarski.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.quizpszczelarski.app.presentation.leaderboard.LeaderboardIntent
import pl.quizpszczelarski.app.presentation.leaderboard.LeaderboardState
import pl.quizpszczelarski.app.ui.components.AppButton
import pl.quizpszczelarski.app.ui.components.AppButtonVariant
import pl.quizpszczelarski.app.ui.components.LeaderboardEntryRow
import pl.quizpszczelarski.app.ui.components.QuizTopBar
import pl.quizpszczelarski.app.ui.components.TabSelector
import pl.quizpszczelarski.app.ui.components.UserPositionCard
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Leaderboard screen — top bar, tab selector, entry rows, user position card.
 *
 * Stateless composable: receives state, emits intents.
 */
@Composable
fun LeaderboardScreen(
    state: LeaderboardState,
    onIntent: (LeaderboardIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Top bar with back navigation
        QuizTopBar(
            title = "Ranking",
            onBackClick = { onIntent(LeaderboardIntent.GoBack) },
        )

        Spacer(modifier = Modifier.height(spacing.lg))

        // Tab selector
        TabSelector(
            tabs = state.tabs,
            selectedIndex = state.selectedTabIndex,
            onTabSelected = { onIntent(LeaderboardIntent.SelectTab(it)) },
            modifier = Modifier.padding(horizontal = spacing.lg),
        )

        Spacer(modifier = Modifier.height(spacing.lg))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = state.errorMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            else -> {
                // Leaderboard entries
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = spacing.lg),
                ) {
                    items(
                        items = state.entries,
                        key = { "${it.uid}-${it.rank}" },
                    ) { entry ->
                        if (entry.isCurrentUser) {
                            // Current user row — tappable to edit nickname
                            Box(
                                modifier = Modifier.clickable {
                                    onIntent(LeaderboardIntent.StartEditNickname)
                                },
                            ) {
                                LeaderboardEntryRow(
                                    rank = entry.rank,
                                    name = entry.name,
                                    score = entry.score,
                                    isCurrentUser = true,
                                )
                            }

                            // Inline edit field
                            if (state.isEditingNickname) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = spacing.sm),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                    ),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(spacing.md),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        OutlinedTextField(
                                            value = state.nicknameInput,
                                            onValueChange = {
                                                onIntent(LeaderboardIntent.UpdateNicknameInput(it))
                                            },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            label = { Text("Nowy nick") },
                                        )
                                        Spacer(modifier = Modifier.width(spacing.sm))
                                        AppButton(
                                            text = "OK",
                                            onClick = {
                                                onIntent(LeaderboardIntent.ConfirmNickname)
                                            },
                                            enabled = state.nicknameInput.trim().isNotEmpty(),
                                            modifier = Modifier.width(80.dp),
                                        )
                                        Spacer(modifier = Modifier.width(spacing.sm))
                                        AppButton(
                                            text = "\u2715",
                                            onClick = {
                                                onIntent(LeaderboardIntent.CancelEditNickname)
                                            },
                                            variant = AppButtonVariant.Secondary,
                                            modifier = Modifier.width(48.dp),
                                        )
                                    }
                                }
                            }
                        } else {
                            LeaderboardEntryRow(
                                rank = entry.rank,
                                name = entry.name,
                                score = entry.score,
                                isCurrentUser = false,
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.sm))
                    }
                }
            }
        }

        // User position card
        if (state.userRank > 0) {
            UserPositionCard(
                rank = state.userRank,
                score = state.userScore,
                modifier = Modifier.padding(spacing.lg),
            )
        }
    }
}
