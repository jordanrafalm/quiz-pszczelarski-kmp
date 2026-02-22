package pl.quizpszczelarski.app.presentation.result

/**
 * One-off effects emitted by ResultViewModel.
 */
sealed interface ResultEffect {
    data object NavigateToQuiz : ResultEffect
    data object NavigateToLeaderboard : ResultEffect
}
