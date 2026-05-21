package pl.quizpszczelarski.app.presentation.gameofday

import kotlinx.datetime.LocalDate

/**
 * UI state for Game of Day feature.
 */
data class GameOfDayState(
    val todayType: GameOfDayType = GameOfDayType.FlappyBee,
    val screenState: ScreenState = ScreenState.Menu,
    val score: Int = 0,
    val isCompleted: Boolean = false,
    val completedDate: LocalDate? = null,
) {
    sealed interface ScreenState {
        data object Menu : ScreenState
        data object Playing : ScreenState
        data class GameOver(val score: Int) : ScreenState
    }
}
