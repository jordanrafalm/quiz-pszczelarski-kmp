package pl.quizpszczelarski.shared.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.quizpszczelarski.shared.data.local.db.QuizDatabase
import pl.quizpszczelarski.shared.data.mapper.QuestionMapper
import pl.quizpszczelarski.shared.domain.model.Question

/**
 * SQLDelight implementation of [LocalQuestionsDataSource].
 */
class SqlDelightQuestionsDataSource(
    private val database: QuizDatabase,
) : LocalQuestionsDataSource {

    private val queries get() = database.quizDatabaseQueries

    override suspend fun getActiveQuestions(
        level: String?,
        category: String?,
    ): List<Question> = withContext(Dispatchers.Default) {
        val entities = when {
            level != null && category != null ->
                queries.selectActiveByLevelAndCategory(level, category).executeAsList()
            level != null ->
                queries.selectActiveByLevel(level).executeAsList()
            category != null ->
                queries.selectActiveByCategory(category).executeAsList()
            else ->
                queries.selectActiveQuestions().executeAsList()
        }
        entities.map { QuestionMapper.entityToDomain(it) }
    }

    override suspend fun countActive(): Long = withContext(Dispatchers.Default) {
        queries.countActive().executeAsOne()
    }

    override suspend fun replaceAll(questions: List<QuestionInsert>) = withContext(Dispatchers.Default) {
        database.transaction {
            queries.deleteAll()
            questions.forEach { q ->
                queries.insertOrReplace(
                    id = q.id,
                    text = q.text,
                    options = q.options,
                    correctAnswer = q.correctAnswer.toLong(),
                    category = q.category,
                    level = q.level,
                    infotip = q.infotip,
                    active = if (q.active) 1L else 0L,
                    type = q.type,
                    updatedAt = q.updatedAtMillis,
                )
            }
        }
    }

    override suspend fun getSyncMeta(key: String): String? = withContext(Dispatchers.Default) {
        queries.getSyncMeta(key).executeAsOneOrNull()
    }

    override suspend fun setSyncMeta(key: String, value: String) = withContext(Dispatchers.Default) {
        queries.setSyncMeta(key, value)
    }
}
