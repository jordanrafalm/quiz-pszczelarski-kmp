package pl.quizpszczelarski.shared.data.local

/**
 * Local persistence for quiz scores that couldn't be submitted
 * due to network issues. Scores are queued and flushed when online.
 */
interface PendingScoreDataSource {

    /** Insert a pending score to be submitted later. */
    suspend fun insert(uid: String, score: Int, createdAt: Long)

    /** Get all pending scores ordered by creation time. */
    suspend fun getAll(): List<PendingScoreRecord>

    /** Delete a single pending score by id (after successful submit). */
    suspend fun delete(id: Long)

    /** Count pending scores. */
    suspend fun count(): Long
}

/**
 * Lightweight record for a pending score.
 */
data class PendingScoreRecord(
    val id: Long,
    val uid: String,
    val score: Int,
    val createdAt: Long,
)
