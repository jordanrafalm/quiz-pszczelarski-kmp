package pl.quizpszczelarski.app.presentation.quiz

import pl.quizpszczelarski.app.platform.ImpactType

/**
 * One-off effects emitted by QuizViewModel.
 */
sealed interface QuizEffect {
    data class NavigateToResult(val score: Int, val total: Int) : QuizEffect
    data class ShowSnackbar(val message: String) : QuizEffect
    /** No questions loaded (empty cache + offline). UI should navigate back. */
    data object NoQuestionsAvailable : QuizEffect
    /** Trigger haptic feedback (gated by settings in UI collector). */
    data class PlayHaptic(val type: ImpactType) : QuizEffect
    /** Exit quiz — navigate back to Home without saving score. */
    data object NavigateToHome : QuizEffect
}
