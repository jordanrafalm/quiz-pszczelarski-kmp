package pl.quizpszczelarski.shared.domain.usecase

import pl.quizpszczelarski.shared.domain.model.Question
import pl.quizpszczelarski.shared.domain.model.QuizResult
import kotlin.math.roundToInt

/**
 * Calculates the quiz score from questions and user answers.
 */
class CalculateScoreUseCase {

    /**
     * @param questions The list of questions in the quiz.
     * @param answers User-selected answer indices (same order as questions).
     * @return [QuizResult] with score, total, and percentage.
     */
    operator fun invoke(
        questions: List<Question>,
        answers: List<Int>,
    ): QuizResult {
        require(questions.size == answers.size) {
            "Questions (${questions.size}) and answers (${answers.size}) must have the same count"
        }

        val score = questions.zip(answers).count { (question, answer) ->
            question.correctAnswerIndex == answer
        }
        val total = questions.size
        val percentage = if (total == 0) 0
        else ((score.toFloat() / total) * 100).roundToInt()

        return QuizResult(
            score = score,
            totalQuestions = total,
            percentage = percentage,
        )
    }
}
