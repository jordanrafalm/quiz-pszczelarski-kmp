package pl.quizpszczelarski.app.presentation.quiz

import pl.quizpszczelarski.shared.domain.model.Question
import pl.quizpszczelarski.shared.domain.repository.SyncResult

/**
 * User actions and internal state transitions on the Quiz screen.
 *
 * Public intents (from UI):
 * - SelectAnswer, CheckAnswer, NextQuestion, DismissBonus, RetryLoad, ExitQuiz
 *
 * Internal intents (from ViewModel logic):
 * - LoadQuestions, ShowLoadError, SyncStarted, SyncCompleted
 */
sealed interface QuizIntent {
    // User-triggered actions
    data class SelectAnswer(val index: Int) : QuizIntent
    data object CheckAnswer : QuizIntent
    data object NextQuestion : QuizIntent
    data object DismissBonus : QuizIntent
    data object RetryLoad : QuizIntent
    data object ExitQuiz : QuizIntent

    // Internal state transitions (ViewModel-driven)
    data class LoadQuestions(val questions: List<Question>) : QuizIntent
    data class ShowLoadError(val message: String) : QuizIntent
    data object SyncStarted : QuizIntent
    data class SyncCompleted(val result: SyncResult) : QuizIntent
}
