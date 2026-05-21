package pl.quizpszczelarski.app.presentation.gameofday

/**
 * User actions on the Game of Day screen.
 */
sealed interface GameOfDayIntent {
    data object LoadGameOfDay : GameOfDayIntent
    data object StartGame : GameOfDayIntent
    data class EndGame(val score: Int) : GameOfDayIntent
    data object RetryGame : GameOfDayIntent
    data object BackToHome : GameOfDayIntent
}
