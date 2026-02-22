package pl.quizpszczelarski.app.presentation.home

import pl.quizpszczelarski.app.platform.ImpactType
import pl.quizpszczelarski.app.presentation.base.MviViewModel

/**
 * ViewModel for the Home screen.
 * Home has no complex state — mainly emits navigation effects.
 */
class HomeViewModel : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

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
            HomeIntent.ToggleHaptics -> {
                emitEffect(HomeEffect.ToggleHaptics)
            }
            HomeIntent.ToggleSound -> {
                emitEffect(HomeEffect.ToggleSound)
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
