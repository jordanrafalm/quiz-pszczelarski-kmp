package pl.quizpszczelarski.app.presentation.leaderboard

/**
 * One-off effects emitted by LeaderboardViewModel.
 */
sealed interface LeaderboardEffect {
    data object NavigateBack : LeaderboardEffect
    data class ShowSnackbar(val message: String) : LeaderboardEffect
}
