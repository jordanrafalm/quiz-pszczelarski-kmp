package pl.quizpszczelarski.shared.data.questions

import pl.quizpszczelarski.shared.data.remote.QuestionWithMeta
import pl.quizpszczelarski.shared.data.remote.RemoteQuestionsDataSource

/**
 * Fake implementation of [RemoteQuestionsDataSource] for testing.
 */
class FakeRemoteQuestionsDataSource : RemoteQuestionsDataSource {

    var syncVersion: Int? = 1
    var questions: List<QuestionWithMeta> = emptyList()
    var shouldFail: Boolean = false

    /** How many times fetchAllActiveQuestions was called. */
    var fetchCallCount: Int = 0

    override suspend fun getSyncVersion(): Int? {
        if (shouldFail) throw RuntimeException("Network error")
        return syncVersion
    }

    override suspend fun fetchAllActiveQuestions(): List<QuestionWithMeta> {
        if (shouldFail) throw RuntimeException("Network error")
        fetchCallCount++
        return questions
    }
}
