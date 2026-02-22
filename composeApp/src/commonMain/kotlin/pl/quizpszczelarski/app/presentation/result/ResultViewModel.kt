package pl.quizpszczelarski.app.presentation.result

import kotlinx.coroutines.launch
import pl.quizpszczelarski.app.platform.ImpactType
import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.data.local.PendingScoreDataSource
import pl.quizpszczelarski.shared.data.util.currentTimeMillis
import pl.quizpszczelarski.shared.domain.repository.SettingsRepository
import pl.quizpszczelarski.shared.domain.repository.UserRepository
import pl.quizpszczelarski.shared.domain.service.AnalyticsService
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
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val analyticsService: AnalyticsService,
) : MviViewModel<ResultState, ResultIntent, ResultEffect>(
    ResultState(score = score, totalQuestions = totalQuestions),
) {

    init {
        // Haptic feedback based on score
        val hapticType = if (state.value.isHighScore) ImpactType.Success else ImpactType.Error
        emitEffect(ResultEffect.PlayHaptic(hapticType))

        submitQuizScore()

        // Show nickname prompt if user hasn't set a custom nickname yet
        if (!settingsRepository.hasCustomNickname()) {
            onIntent(ResultIntent.ShowNicknameDialog)
        }
    }

    private fun submitQuizScore() {
        if (uid == null) return
        scope.launch {
            try {
                submitScore(uid, state.value.score)
            } catch (e: Exception) {
                // Submit failed (offline) — queue score locally
                analyticsService.recordNonFatal(e, mapOf(
                    "context" to "submit_score",
                    "score" to state.value.score.toString(),
                ))
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
            ResultIntent.GoBack -> emitEffect(ResultEffect.NavigateToHome)
            ResultIntent.PlayAgain -> emitEffect(ResultEffect.NavigateToHome)
            ResultIntent.ViewLeaderboard -> emitEffect(ResultEffect.NavigateToLeaderboard)
            ResultIntent.ShowNicknameDialog -> return state.copy(showNicknamePrompt = true)
            is ResultIntent.UpdateNicknameInput -> return state.copy(nicknameInput = intent.text)
            ResultIntent.DismissNicknameDialog -> {
                // Mark as seen so it doesn't show on subsequent games
                scope.launch {
                    try { settingsRepository.setHasCustomNickname(true) } catch (_: Exception) {}
                }
                return state.copy(showNicknamePrompt = false)
            }
            ResultIntent.ConfirmNickname -> {
                val nick = state.nicknameInput.trim()
                if (nick.isNotEmpty() && uid != null) {
                    scope.launch {
                        try {
                            userRepository.updateNickname(uid, nick)
                            settingsRepository.setHasCustomNickname(true)
                        } catch (_: Exception) {
                            emitEffect(ResultEffect.ShowError("Nie uda\u0142o si\u0119 zapisa\u0107 nicku"))
                        }
                    }
                }
                return state.copy(showNicknamePrompt = false, nicknameInput = "")
            }
        }
        return state
    }
}
