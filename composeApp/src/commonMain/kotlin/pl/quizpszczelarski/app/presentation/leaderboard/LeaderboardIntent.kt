package pl.quizpszczelarski.app.presentation.leaderboard

/**
 * User actions on the Leaderboard screen.
 */
sealed interface LeaderboardIntent {
    data class SelectTab(val index: Int) : LeaderboardIntent
    data object GoBack : LeaderboardIntent
}
