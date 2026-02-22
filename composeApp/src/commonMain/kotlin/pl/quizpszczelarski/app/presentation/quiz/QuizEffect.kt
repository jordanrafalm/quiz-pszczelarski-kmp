package pl.quizpszczelarski.app.presentation.quiz

/**
 * One-off effects emitted by QuizViewModel.
 */
sealed interface QuizEffect {
    data class NavigateToResult(val score: Int, val total: Int) : QuizEffect
    data class ShowSnackbar(val message: String) : QuizEffect
    /** No questions loaded (empty cache + offline). UI should navigate back. */
    data object NoQuestionsAvailable : QuizEffect
}
