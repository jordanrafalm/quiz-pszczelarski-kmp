package pl.quizpszczelarski.app.presentation.gameofday

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.data.gameofday.GameOfDayRepository

/**
 * ViewModel for the Game of Day screen.
 * Manages game state, type selection (via date), and persistence.
 */
class GameOfDayViewModel(
    private val gameOfDayRepository: GameOfDayRepository? = null,
) : MviViewModel<GameOfDayState, GameOfDayIntent, GameOfDayEffect>(
    initialState = GameOfDayState()
) {

    override fun reduce(state: GameOfDayState, intent: GameOfDayIntent): GameOfDayState {
        return when (intent) {
            GameOfDayIntent.LoadGameOfDay -> {
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val todayType = GameOfDayType.fromDate(today)
                val completedToday = gameOfDayRepository?.isCompletedToday() ?: false
                val lastScore = if (completedToday) gameOfDayRepository?.getLastScore() ?: 0 else 0

                state.copy(
                    todayType = todayType,
                    isCompleted = completedToday,
                    completedDate = if (completedToday) today else null,
                    score = lastScore,
                    screenState = GameOfDayState.ScreenState.Menu,
                )
            }

            GameOfDayIntent.StartGame -> {
                state.copy(screenState = GameOfDayState.ScreenState.Playing)
            }

            is GameOfDayIntent.EndGame -> {
                gameOfDayRepository?.saveGameResult(intent.score)
                state.copy(
                    screenState = GameOfDayState.ScreenState.GameOver(intent.score),
                    score = intent.score,
                    isCompleted = true,
                    completedDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                )
            }

            GameOfDayIntent.RetryGame -> {
                state.copy(screenState = GameOfDayState.ScreenState.Playing)
            }

            GameOfDayIntent.BackToHome -> {
                emitEffect(GameOfDayEffect.NavigateBack)
                state
            }

            is GameOfDayIntent.SelectGameType -> {
                state.copy(
                    todayType = intent.type,
                    screenState = GameOfDayState.ScreenState.Menu,
                )
            }
        }
    }
}
