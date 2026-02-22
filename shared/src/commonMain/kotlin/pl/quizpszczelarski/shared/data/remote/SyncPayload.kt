package pl.quizpszczelarski.shared.data.remote

import pl.quizpszczelarski.shared.data.dto.QuestionDto

/**
 * Result of fetching a question + metadata from Firestore.
 */
data class QuestionWithMeta(
    val id: String,
    val dto: QuestionDto,
    val updatedAtMillis: Long,
)

/**
 * Result of reading the sync version from Firestore.
 */
data class SyncSnapshot(
    val version: Int,
    val questions: List<QuestionWithMeta>,
)
