package pl.quizpszczelarski.app.presentation.leaderboard

/**
 * User actions on the Leaderboard screen.
 */
sealed interface LeaderboardIntent {
    data class SelectTab(val index: Int) : LeaderboardIntent
    data object GoBack : LeaderboardIntent
    data object StartEditNickname : LeaderboardIntent
    data class UpdateNicknameInput(val text: String) : LeaderboardIntent
    data object ConfirmNickname : LeaderboardIntent
    data object CancelEditNickname : LeaderboardIntent
}
