package pl.quizpszczelarski.app.presentation.quiz

/**
 * One-off effects emitted by QuizViewModel.
 */
sealed interface QuizEffect {
    data class NavigateToResult(val score: Int, val total: Int) : QuizEffect
}
