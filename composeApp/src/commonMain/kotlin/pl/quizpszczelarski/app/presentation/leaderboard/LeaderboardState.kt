package pl.quizpszczelarski.app.presentation.leaderboard

import pl.quizpszczelarski.shared.domain.model.LeaderboardEntry

/**
 * Immutable state for the Leaderboard screen.
 */
data class LeaderboardState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val selectedTabIndex: Int = 0,
    val isLoading: Boolean = false,
    val userRank: Int = 0,
    val userScore: Int = 0,
) {
    val tabs: List<String> = listOf("All-time", "Weekly")
}
