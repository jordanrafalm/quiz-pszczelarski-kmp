package pl.quizpszczelarski.shared.domain.repository

import pl.quizpszczelarski.shared.domain.model.Question

/**
 * Contract for accessing quiz questions.
 * Implemented in the data layer.
 */
interface QuestionRepository {

    /**
     * Returns all available questions.
     */
    fun getAllQuestions(): List<Question>
}
