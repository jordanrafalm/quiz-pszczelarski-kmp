package pl.quizpszczelarski.app.presentation.result

import pl.quizpszczelarski.app.presentation.base.MviViewModel

/**
 * ViewModel for the Result screen.
 * Receives score/total from navigation args. Emits navigation effects.
 */
class ResultViewModel(
    score: Int,
    totalQuestions: Int,
) : MviViewModel<ResultState, ResultIntent, ResultEffect>(
    ResultState(score = score, totalQuestions = totalQuestions),
) {

    override fun reduce(state: ResultState, intent: ResultIntent): ResultState {
        when (intent) {
            ResultIntent.PlayAgain -> emitEffect(ResultEffect.NavigateToQuiz)
            ResultIntent.ViewLeaderboard -> emitEffect(ResultEffect.NavigateToLeaderboard)
        }
        return state
    }
}
