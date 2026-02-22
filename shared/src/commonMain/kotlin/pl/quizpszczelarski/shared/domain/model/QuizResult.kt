package pl.quizpszczelarski.shared.domain.model

/**
 * Completed quiz result.
 *
 * @param score Number of correct answers.
 * @param totalQuestions Total number of questions in the quiz.
 * @param percentage Score as a percentage (0-100).
 */
data class QuizResult(
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
)
