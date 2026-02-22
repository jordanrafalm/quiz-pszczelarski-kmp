package pl.quizpszczelarski.shared.data.questions

import kotlinx.coroutines.test.runTest
import pl.quizpszczelarski.shared.data.dto.QuestionDto
import pl.quizpszczelarski.shared.data.remote.QuestionWithMeta
import pl.quizpszczelarski.shared.domain.repository.SyncResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CachingQuestionsRepositoryTest {

    private fun createTestQuestions(): List<QuestionWithMeta> = listOf(
        QuestionWithMeta(
            id = "q1",
            dto = QuestionDto(
                text = "Ile oczu ma pszczoła?",
                options = listOf("2", "5", "8", "10"),
                correctAnswer = 1,
                category = "biologia",
                level = "podstawowy",
                infotip = "Pszczoła ma 5 oczu",
                active = true,
                type = "SINGLE",
            ),
            updatedAtMillis = 1000L,
        ),
        QuestionWithMeta(
            id = "q2",
            dto = QuestionDto(
                text = "Ile skrzydeł ma pszczoła?",
                options = listOf("2", "4", "6", "8"),
                correctAnswer = 1,
                category = "biologia",
                level = "podstawowy",
                infotip = "Pszczoła ma 4 skrzydła",
                active = true,
                type = "SINGLE",
            ),
            updatedAtMillis = 2000L,
        ),
    )

    // --- cache-first ---

    @Test
    fun getActiveQuestions_returnsCachedQuestions_whenCacheIsNonEmpty() = runTest {
        val local = FakeLocalQuestionsDataSource()
        val remote = FakeRemoteQuestionsDataSource()
        remote.questions = createTestQuestions()

        val repo = CachingQuestionsRepository(local, remote)

        // First call populates cache from remote
        repo.getActiveQuestions()

        // Reset fetch counter
        remote.fetchCallCount = 0

        // Second call should return from cache without fetching
        val result = repo.getActiveQuestions()

        assertEquals(2, result.size)
        assertEquals(0, remote.fetchCallCount)
    }

    @Test
    fun getActiveQuestions_fetchesFromRemote_whenCacheIsEmpty() = runTest {
        val local = FakeLocalQuestionsDataSource()
        val remote = FakeRemoteQuestionsDataSource()
        remote.questions = createTestQuestions()

        val repo = CachingQuestionsRepository(local, remote)

        val result = repo.getActiveQuestions()

        assertEquals(2, result.size)
        assertEquals(1, remote.fetchCallCount)
        assertEquals(1, local.replaceAllCallCount)
    }

    @Test
    fun getActiveQuestions_returnsEmptyList_whenCacheEmptyAndRemoteFails() = runTest {
        val local = FakeLocalQuestionsDataSource()
        val remote = FakeRemoteQuestionsDataSource()
        remote.shouldFail = true

        val repo = CachingQuestionsRepository(local, remote)

        val result = repo.getActiveQuestions()

        assertTrue(result.isEmpty())
    }

    // --- syncQuestionsIfNeeded ---

    @Test
    fun syncQuestionsIfNeeded_returnsUpToDate_whenVersionsMatch() = runTest {
        val local = FakeLocalQuestionsDataSource()
        val remote = FakeRemoteQuestionsDataSource()
        remote.syncVersion = 3
        remote.questions = createTestQuestions()

        local.setSyncMeta("lastSyncVersion", "3")

        val repo = CachingQuestionsRepository(local, remote)

        val result = repo.syncQuestionsIfNeeded()

        assertEquals(SyncResult.UP_TO_DATE, result)
        assertEquals(0, remote.fetchCallCount) // No fetch needed
    }

    @Test
    fun syncQuestionsIfNeeded_returnsUpdated_whenVersionsDiffer() = runTest {
        val local = FakeLocalQuestionsDataSource()
        val remote = FakeRemoteQuestionsDataSource()
        remote.syncVersion = 3
        remote.questions = createTestQuestions()

        local.setSyncMeta("lastSyncVersion", "2")

        val repo = CachingQuestionsRepository(local, remote)

        val result = repo.syncQuestionsIfNeeded()

        assertEquals(SyncResult.UPDATED, result)
        assertEquals(1, remote.fetchCallCount)
        assertEquals(1, local.replaceAllCallCount)
    }

    @Test
    fun syncQuestionsIfNeeded_returnsNoMetaDoc_whenRemoteHasNoMeta() = runTest {
        val local = FakeLocalQuestionsDataSource()
        val remote = FakeRemoteQuestionsDataSource()
        remote.syncVersion = null

        val repo = CachingQuestionsRepository(local, remote)

        val result = repo.syncQuestionsIfNeeded()

        assertEquals(SyncResult.NO_META_DOC, result)
        assertEquals(0, remote.fetchCallCount)
    }

    @Test
    fun syncQuestionsIfNeeded_returnsFailed_whenRemoteThrows() = runTest {
        val local = FakeLocalQuestionsDataSource()
        val remote = FakeRemoteQuestionsDataSource()
        remote.shouldFail = true

        val repo = CachingQuestionsRepository(local, remote)

        val result = repo.syncQuestionsIfNeeded()

        assertEquals(SyncResult.FAILED, result)
    }

    @Test
    fun syncQuestionsIfNeeded_storesNewVersionOnSuccess() = runTest {
        val local = FakeLocalQuestionsDataSource()
        val remote = FakeRemoteQuestionsDataSource()
        remote.syncVersion = 5
        remote.questions = createTestQuestions()

        val repo = CachingQuestionsRepository(local, remote)

        repo.syncQuestionsIfNeeded()

        assertEquals("5", local.getSyncMeta("lastSyncVersion"))
    }

    // --- forceRefreshQuestions ---

    @Test
    fun forceRefreshQuestions_redownloads_evenWhenVersionsMatch() = runTest {
        val local = FakeLocalQuestionsDataSource()
        val remote = FakeRemoteQuestionsDataSource()
        remote.syncVersion = 3
        remote.questions = createTestQuestions()

        local.setSyncMeta("lastSyncVersion", "3")

        val repo = CachingQuestionsRepository(local, remote)

        val result = repo.forceRefreshQuestions()

        assertEquals(SyncResult.UPDATED, result)
        assertEquals(1, remote.fetchCallCount) // Always fetches
    }

    @Test
    fun forceRefreshQuestions_returnsFailed_whenRemoteThrows() = runTest {
        val local = FakeLocalQuestionsDataSource()
        val remote = FakeRemoteQuestionsDataSource()
        remote.shouldFail = true

        val repo = CachingQuestionsRepository(local, remote)

        val result = repo.forceRefreshQuestions()

        assertEquals(SyncResult.FAILED, result)
    }
}
