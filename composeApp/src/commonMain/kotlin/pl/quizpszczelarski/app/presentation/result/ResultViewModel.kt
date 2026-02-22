package pl.quizpszczelarski.app.presentation.result

import kotlinx.coroutines.launch
import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.domain.usecase.SubmitScoreUseCase

/**
 * ViewModel for the Result screen.
 * Receives score/total from navigation args. Submits score to Firestore.
 */
class ResultViewModel(
    score: Int,
    totalQuestions: Int,
    private val submitScore: SubmitScoreUseCase? = null,
    private val uid: String? = null,
) : MviViewModel<ResultState, ResultIntent, ResultEffect>(
    ResultState(score = score, totalQuestions = totalQuestions),
) {

    init {
        submitQuizScore()
    }

    private fun submitQuizScore() {
        if (uid == null || submitScore == null) return
        scope.launch {
            try {
                submitScore(uid, state.value.score)
            } catch (e: Exception) {
                emitEffect(ResultEffect.ShowError("Nie udało się zapisać wyniku"))
            }
        }
    }

    override fun reduce(state: ResultState, intent: ResultIntent): ResultState {
        when (intent) {
            ResultIntent.PlayAgain -> emitEffect(ResultEffect.NavigateToQuiz)
            ResultIntent.ViewLeaderboard -> emitEffect(ResultEffect.NavigateToLeaderboard)
        }
        return state
    }
}
