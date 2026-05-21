package pl.quizpszczelarski.app.presentation.gameofday

/**
 * One-off effects emitted by GameOfDayViewModel.
 */
sealed interface GameOfDayEffect {
    data object NavigateBack : GameOfDayEffect
    data class ShowSnackbar(val message: String) : GameOfDayEffect
}
