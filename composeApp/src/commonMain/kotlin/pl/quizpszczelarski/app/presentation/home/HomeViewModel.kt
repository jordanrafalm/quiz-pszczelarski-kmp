package pl.quizpszczelarski.app.presentation.home

import pl.quizpszczelarski.app.platform.ImpactType
import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.data.gameofday.GameOfDayRepository

/**
 * ViewModel for the Home screen.
 * Home has no complex state — mainly emits navigation effects.
 *
 * Reads Game of Day completion state from [gameOfDayRepository] at construction so the
 * badge on the "Gra Dnia" card is always fresh when the user returns to Home.
 */
class HomeViewModel(
    newQuestionsAvailable: Boolean = false,
    gameOfDayRepository: GameOfDayRepository? = null,
) : MviViewModel<HomeState, HomeIntent, HomeEffect>(
    HomeState(
        newQuestionsAvailable = newQuestionsAvailable,
        gameOfDayCompleted = gameOfDayRepository?.isCompletedToday() ?: false,
        gameOfDayScore = gameOfDayRepository?.getLastScore() ?: 0,
    )
) {

    override fun reduce(state: HomeState, intent: HomeIntent): HomeState {
        when (intent) {
            HomeIntent.StartQuiz -> {
                emitEffect(HomeEffect.PlayHaptic(ImpactType.Light))
                return state.copy(showLevelSelect = true)
            }
            HomeIntent.ViewLeaderboard -> {
                emitEffect(HomeEffect.PlayHaptic(ImpactType.Light))
                emitEffect(HomeEffect.NavigateToLeaderboard)
            }
            HomeIntent.ViewGameOfDay -> {
                emitEffect(HomeEffect.PlayHaptic(ImpactType.Light))
                emitEffect(HomeEffect.NavigateToGameOfDay)
            }
            HomeIntent.ToggleHaptics -> {
                emitEffect(HomeEffect.ToggleHaptics)
            }
            HomeIntent.ToggleSound -> {
                emitEffect(HomeEffect.ToggleSound)
            }
            HomeIntent.ToggleNotifications -> {
                emitEffect(HomeEffect.ToggleNotifications)
            }
            is HomeIntent.SelectLevel -> {
                emitEffect(HomeEffect.PlayHaptic(ImpactType.Medium))
                emitEffect(HomeEffect.NavigateToQuiz(intent.level, state.selectedQuestionCount))
                return state.copy(showLevelSelect = false)
            }
            HomeIntent.BackFromLevelSelect -> {
                return state.copy(showLevelSelect = false)
            }
            is HomeIntent.SetQuestionCount -> {
                return state.copy(selectedQuestionCount = intent.count)
            }
        }
        return state
    }
}
