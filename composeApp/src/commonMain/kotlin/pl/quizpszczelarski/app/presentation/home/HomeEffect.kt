package pl.quizpszczelarski.app.presentation.home

/**
 * One-off effects emitted by HomeViewModel.
 */
sealed interface HomeEffect {
    data object NavigateToQuiz : HomeEffect
    data object NavigateToLeaderboard : HomeEffect
}
