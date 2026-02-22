package pl.quizpszczelarski.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pl.quizpszczelarski.app.presentation.leaderboard.LeaderboardState
import pl.quizpszczelarski.app.ui.theme.AppTheme
import pl.quizpszczelarski.shared.domain.model.LeaderboardEntry

@Preview(showBackground = true)
@Composable
fun LeaderboardScreenPreview() {
    AppTheme {
        LeaderboardScreen(
            state = LeaderboardState(
                entries = listOf(
                    LeaderboardEntry(
                        uid = "1",
                        rank = 1,
                        name = "Mistrz Pszczół",
                        score = 950,
                        isCurrentUser = false,
                    ),
                    LeaderboardEntry(
                        uid = "2",
                        rank = 2,
                        name = "Pszczelarz123",
                        score = 820,
                        isCurrentUser = true,
                    ),
                    LeaderboardEntry(
                        uid = "3",
                        rank = 3,
                        name = "BeeLover",
                        score = 750,
                        isCurrentUser = false,
                    ),
                ),
                selectedTabIndex = 0,
                isLoading = false,
                userRank = 2,
                userScore = 820,
            ),
            onIntent = {},
        )
    }
}
