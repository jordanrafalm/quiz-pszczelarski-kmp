package pl.quizpszczelarski.app.presentation.result

import pl.quizpszczelarski.app.platform.ImpactType

/**
 * One-off effects emitted by ResultViewModel.
 */
sealed interface ResultEffect {
    data object NavigateToHome : ResultEffect
    data object NavigateToLeaderboard : ResultEffect
    /** Navigate to a new quiz with the same level and question count as the last game. */
    data class RestartQuizWithSameParams(val level: String, val questionCount: Int) : ResultEffect
    data class ShowError(val message: String) : ResultEffect
    /** Trigger haptic feedback (gated by settings in UI collector). */
    data class PlayHaptic(val type: ImpactType) : ResultEffect
}
