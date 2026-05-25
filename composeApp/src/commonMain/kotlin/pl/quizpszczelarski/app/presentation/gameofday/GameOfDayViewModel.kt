package pl.quizpszczelarski.app.presentation.gameofday

import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.data.gameofday.GameOfDayRepository
import pl.quizpszczelarski.shared.domain.repository.UserRepository

/**
 * ViewModel for the Game of Day screen.
 * Manages game state, type selection (via date), persistence, and score integration with Firestore.
 */
class GameOfDayViewModel(
    private val gameOfDayRepository: GameOfDayRepository? = null,
    private val userRepository: UserRepository? = null,
    private val uid: String? = null,
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
                val alreadyCompleted = gameOfDayRepository?.isCompletedToday() ?: false
                
                if (!alreadyCompleted && uid != null) {
                    // First game of the day — add score to local storage and Firestore
                    scope.launch {
                        try {
                            gameOfDayRepository?.saveGameResult(intent.score)
                            userRepository?.addScore(uid, intent.score)
                            emitEffect(GameOfDayEffect.ShowSnackbar("✅ Dodano ${intent.score} pkt do rankingu!"))
                        } catch (e: Exception) {
                            emitEffect(GameOfDayEffect.ShowSnackbar("⚠️ Błąd: ${e.message}"))
                        }
                    }
                } else if (alreadyCompleted) {
                    emitEffect(GameOfDayEffect.ShowSnackbar("⚠️ Już zagrałeś dzisiaj! Punkty nie liczą się ponownie."))
                }
                
                state.copy(
                    screenState = GameOfDayState.ScreenState.GameOver(
                        score = intent.score,
                        pointsAdded = !alreadyCompleted && uid != null,
                    ),
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
