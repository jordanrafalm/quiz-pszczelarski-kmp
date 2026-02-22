package pl.quizpszczelarski.app.presentation.home

import pl.quizpszczelarski.app.presentation.base.MviViewModel

/**
 * ViewModel for the Home screen.
 * Home has no complex state — mainly emits navigation effects.
 */
class HomeViewModel : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    override fun reduce(state: HomeState, intent: HomeIntent): HomeState {
        when (intent) {
            HomeIntent.StartQuiz -> emitEffect(HomeEffect.NavigateToQuiz)
            HomeIntent.ViewLeaderboard -> emitEffect(HomeEffect.NavigateToLeaderboard)
        }
        return state
    }
}
