package pl.quizpszczelarski.app.presentation.result

import kotlinx.coroutines.launch
import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.data.local.PendingScoreDataSource
import pl.quizpszczelarski.shared.data.util.currentTimeMillis
import pl.quizpszczelarski.shared.domain.usecase.SubmitScoreUseCase

/**
 * ViewModel for the Result screen.
 * Receives score/total from navigation args. Submits score to Firestore.
 * If offline — queues the score locally for later submission.
 */
class ResultViewModel(
    score: Int,
    totalQuestions: Int,
    private val submitScore: SubmitScoreUseCase,
    private val uid: String?,
    private val pendingScoreDataSource: PendingScoreDataSource,
) : MviViewModel<ResultState, ResultIntent, ResultEffect>(
    ResultState(score = score, totalQuestions = totalQuestions),
) {

    init {
        submitQuizScore()
    }

    private fun submitQuizScore() {
        if (uid == null) return
        scope.launch {
            try {
                submitScore(uid, state.value.score)
            } catch (e: Exception) {
                // Submit failed (offline) — queue score locally
                try {
                    pendingScoreDataSource.insert(
                        uid = uid,
                        score = state.value.score,
                        createdAt = currentTimeMillis(),
                    )
                    emitEffect(ResultEffect.ShowError("Wynik zapisany lokalnie — wyślemy go przy połączeniu z internetem"))
                } catch (_: Exception) {
                    emitEffect(ResultEffect.ShowError("Nie udało się zapisać wyniku"))
                }
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
