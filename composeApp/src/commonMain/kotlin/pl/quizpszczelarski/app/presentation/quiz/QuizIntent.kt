package pl.quizpszczelarski.app.presentation.quiz

/**
 * User actions on the Quiz screen.
 */
sealed interface QuizIntent {
    data class SelectAnswer(val index: Int) : QuizIntent
    data object NextQuestion : QuizIntent
    data object RetryLoad : QuizIntent
}
