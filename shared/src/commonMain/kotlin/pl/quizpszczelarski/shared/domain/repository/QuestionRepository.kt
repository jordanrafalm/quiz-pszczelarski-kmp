package pl.quizpszczelarski.shared.domain.repository

import pl.quizpszczelarski.shared.domain.model.Question

/**
 * Contract for accessing quiz questions.
 * Implemented in the data layer (Firebase or local fallback).
 */
interface QuestionRepository {

    /**
     * Returns active questions with optional filters.
     *
     * @param level Filter by difficulty level (null = all).
     * @param category Filter by category (null = all).
     * @param limit Maximum number of questions to return.
     */
    suspend fun getActiveQuestions(
        level: String? = null,
        category: String? = null,
        limit: Int = 200,
    ): List<Question>
}
