package pl.quizpszczelarski.app.presentation.gameofday

import kotlinx.coroutines.launch
import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.data.gameofday.GameOfDayRepository
import pl.quizpszczelarski.shared.domain.repository.UserRepository
import pl.quizpszczelarski.shared.domain.util.todayLocalDate

/**
 * ViewModel for the Game of Day screen.
 * Manages game state, persistence, and score integration with Firestore.
 *
 * On [GameOfDayIntent.LoadGameOfDay]:
 *  - If already completed today → shows [GameOfDayState.ScreenState.GameOver] with previous score
 *  - Otherwise → goes directly to [GameOfDayState.ScreenState.Playing] (no menu)
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
                val today = todayLocalDate()
                val todayType = GameOfDayType.fromDate(today)
                val completedToday = gameOfDayRepository?.isCompletedToday() ?: false
                val lastScore = if (completedToday) gameOfDayRepository?.getLastScore() ?: 0 else 0

                state.copy(
                    todayType = todayType,
                    isCompleted = completedToday,
                    completedDate = if (completedToday) today else null,
                    score = lastScore,
                    // Go directly to Playing; if already completed show GameOver
                    screenState = if (completedToday) {
                        GameOfDayState.ScreenState.GameOver(
                            score = lastScore,
                            pointsAdded = false,
                        )
                    } else {
                        GameOfDayState.ScreenState.Playing
                    },
                )
            }

            is GameOfDayIntent.EndGame -> {
                val alreadyCompleted = gameOfDayRepository?.isCompletedToday() ?: false

                if (!alreadyCompleted && uid != null) {
                    // First game of the day — save score and add to ranking
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
                    completedDate = todayLocalDate(),
                )
            }

            GameOfDayIntent.RetryGame -> {
                state.copy(screenState = GameOfDayState.ScreenState.Playing)
            }

            GameOfDayIntent.BackToHome -> {
                emitEffect(GameOfDayEffect.NavigateBack)
                state
            }
        }
    }
}
