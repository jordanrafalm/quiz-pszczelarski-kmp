package pl.quizpszczelarski.shared.data.questions

import pl.quizpszczelarski.shared.data.local.LocalQuestionsDataSource
import pl.quizpszczelarski.shared.data.local.QuestionInsert
import pl.quizpszczelarski.shared.domain.model.Question

/**
 * Fake implementation of [LocalQuestionsDataSource] for testing.
 */
class FakeLocalQuestionsDataSource : LocalQuestionsDataSource {

    private val questions = mutableListOf<QuestionInsert>()
    private val meta = mutableMapOf<String, String>()

    /** How many times replaceAll was called. */
    var replaceAllCallCount: Int = 0

    override suspend fun getActiveQuestions(
        level: String?,
        category: String?,
    ): List<Question> {
        return questions
            .filter { it.active }
            .filter { level == null || it.level == level }
            .filter { category == null || it.category == category }
            .map { insert ->
                Question(
                    id = insert.id,
                    text = insert.text,
                    options = kotlinx.serialization.json.Json.decodeFromString<List<String>>(insert.options),
                    correctAnswerIndex = insert.correctAnswer,
                    category = insert.category,
                    level = insert.level,
                    infotip = insert.infotip,
                )
            }
    }

    override suspend fun countActive(): Long = questions.count { it.active }.toLong()

    override suspend fun replaceAll(questions: List<QuestionInsert>) {
        replaceAllCallCount++
        this.questions.clear()
        this.questions.addAll(questions)
    }

    override suspend fun getSyncMeta(key: String): String? = meta[key]

    override suspend fun setSyncMeta(key: String, value: String) {
        meta[key] = value
    }
}
