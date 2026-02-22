package pl.quizpszczelarski.shared.data.local

import pl.quizpszczelarski.shared.domain.model.Question

/**
 * Local persistence for questions (read/write).
 */
interface LocalQuestionsDataSource {

    /** Returns all active questions from local DB, optionally filtered. */
    suspend fun getActiveQuestions(
        level: String? = null,
        category: String? = null,
    ): List<Question>

    /** Returns count of active questions in local DB. */
    suspend fun countActive(): Long

    /** Replaces all questions in local DB (full sync). */
    suspend fun replaceAll(questions: List<QuestionInsert>)

    /** Reads sync metadata value by key. */
    suspend fun getSyncMeta(key: String): String?

    /** Writes sync metadata value by key. */
    suspend fun setSyncMeta(key: String, value: String)
}

/**
 * Insert model for writing a question to local DB.
 * Decoupled from domain model to include storage-specific fields.
 */
data class QuestionInsert(
    val id: String,
    val text: String,
    val options: String,        // JSON-encoded list
    val correctAnswer: Int,
    val category: String,
    val level: String,
    val infotip: String,
    val active: Boolean,
    val type: String,
    val updatedAtMillis: Long,
)
