package pl.quizpszczelarski.app.presentation.home

/**
 * User actions on the Home screen.
 */
sealed interface HomeIntent {
    data object StartQuiz : HomeIntent
    data object ViewLeaderboard : HomeIntent
}
