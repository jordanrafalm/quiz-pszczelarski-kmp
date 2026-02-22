package pl.quizpszczelarski.shared.domain.usecase

import pl.quizpszczelarski.shared.domain.model.Question
import pl.quizpszczelarski.shared.domain.repository.QuestionRepository

/**
 * Returns a random subset of questions for a quiz session.
 *
 * @param repository Source of quiz questions.
 */
class GetRandomQuestionsUseCase(
    private val repository: QuestionRepository,
) {

    /**
     * @param count Number of questions to return (default 5).
     * @return Shuffled list of [count] questions.
     */
    suspend operator fun invoke(count: Int = 5): List<Question> {
        return repository.getActiveQuestions()
            .shuffled()
            .take(count)
    }
}
