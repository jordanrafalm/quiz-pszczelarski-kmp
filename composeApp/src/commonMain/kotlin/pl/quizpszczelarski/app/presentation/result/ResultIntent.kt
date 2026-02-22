package pl.quizpszczelarski.app.presentation.result

/**
 * User actions on the Result screen.
 */
sealed interface ResultIntent {
    data object PlayAgain : ResultIntent
    data object ViewLeaderboard : ResultIntent
}
