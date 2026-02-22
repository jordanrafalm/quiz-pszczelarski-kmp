package pl.quizpszczelarski.shared.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.quizpszczelarski.shared.data.local.db.QuizDatabase

/**
 * SQLDelight implementation of [PendingScoreDataSource].
 */
class SqlDelightPendingScoreDataSource(
    private val database: QuizDatabase,
) : PendingScoreDataSource {

    private val queries get() = database.quizDatabaseQueries

    override suspend fun insert(uid: String, score: Int, createdAt: Long) =
        withContext(Dispatchers.Default) {
            queries.insertPendingScore(
                uid = uid,
                score = score.toLong(),
                createdAt = createdAt,
            )
        }

    override suspend fun getAll(): List<PendingScoreRecord> =
        withContext(Dispatchers.Default) {
            queries.selectAllPendingScores().executeAsList().map { entity ->
                PendingScoreRecord(
                    id = entity.id,
                    uid = entity.uid,
                    score = entity.score.toInt(),
                    createdAt = entity.createdAt,
                )
            }
        }

    override suspend fun delete(id: Long) =
        withContext(Dispatchers.Default) {
            queries.deletePendingScore(id)
        }

    override suspend fun count(): Long =
        withContext(Dispatchers.Default) {
            queries.countPendingScores().executeAsOne()
        }
}
