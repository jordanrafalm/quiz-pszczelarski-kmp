package pl.quizpszczelarski.shared.data.questions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import pl.quizpszczelarski.shared.data.local.LocalQuestionsDataSource
import pl.quizpszczelarski.shared.data.local.QuestionInsert
import pl.quizpszczelarski.shared.data.mapper.QuestionMapper
import pl.quizpszczelarski.shared.data.remote.RemoteQuestionsDataSource
import pl.quizpszczelarski.shared.data.util.currentTimeMillis
import pl.quizpszczelarski.shared.domain.model.Question
import pl.quizpszczelarski.shared.domain.repository.QuestionRepository
import pl.quizpszczelarski.shared.domain.repository.QuestionSyncService
import pl.quizpszczelarski.shared.domain.repository.SyncResult

/**
 * Cache-first question loading with background Firestore sync.
 *
 * Algorithm:
 * 1. getActiveQuestions() → reads from local DB.
 *    - If cache is non-empty → returns immediately.
 *    - If cache is empty (first launch) → fetches from remote, stores, returns.
 * 2. syncQuestionsIfNeeded() → compares local version with remote meta doc.
 *    - If version mismatch → re-downloads all active questions, replaces local DB.
 * 3. forceRefreshQuestions() → unconditionally re-downloads + replaces.
 */
class CachingQuestionsRepository(
    private val local: LocalQuestionsDataSource,
    private val remote: RemoteQuestionsDataSource,
) : QuestionRepository, QuestionSyncService {

    companion object {
        const val KEY_LAST_SYNC_VERSION = "lastSyncVersion"
        const val KEY_LAST_SYNC_AT = "lastSyncAt"
    }

    private val _syncState = MutableStateFlow(SyncStatus.IDLE)
    val syncState: StateFlow<SyncStatus> = _syncState.asStateFlow()

    /** Prevents concurrent sync operations (splash + quiz can trigger in parallel). */
    private val syncMutex = Mutex()

    /**
     * Cache-first: returns local questions if available.
     * On first launch (empty cache), blocks on remote fetch.
     */
    override suspend fun getActiveQuestions(
        level: String?,
        category: String?,
        limit: Int,
    ): List<Question> {
        val cached = local.getActiveQuestions(level, category)
        if (cached.isNotEmpty()) {
            return cached.take(limit)
        }

        // Empty cache (first launch) — must fetch from remote
        return try {
            _syncState.value = SyncStatus.SYNCING
            val result = fetchAndStore()
            _syncState.value = SyncStatus.IDLE
            result.take(limit)
        } catch (e: Exception) {
            _syncState.value = SyncStatus.ERROR
            emptyList()
        }
    }

    /**
     * Checks remote meta doc version and syncs if needed.
     * Call this in background after UI is already showing cached data.
     *
     * @return SyncResult indicating what happened.
     */
    override suspend fun syncQuestionsIfNeeded(): SyncResult = syncMutex.withLock {
        try {
            _syncState.value = SyncStatus.SYNCING
            val remoteVersion = remote.getSyncVersion()
            val localVersion = local.getSyncMeta(KEY_LAST_SYNC_VERSION)?.toIntOrNull()

            if (remoteVersion == null) {
                _syncState.value = SyncStatus.IDLE
                return SyncResult.NO_META_DOC
            }

            if (localVersion != null && localVersion == remoteVersion) {
                _syncState.value = SyncStatus.IDLE
                return SyncResult.UP_TO_DATE
            }

            fetchAndStore()
            local.setSyncMeta(KEY_LAST_SYNC_VERSION, remoteVersion.toString())
            local.setSyncMeta(KEY_LAST_SYNC_AT, currentTimeMillis().toString())

            _syncState.value = SyncStatus.IDLE
            SyncResult.UPDATED
        } catch (e: Exception) {
            _syncState.value = SyncStatus.ERROR
            SyncResult.FAILED
        }
    }

    /**
     * Forces a full re-download regardless of version.
     */
    suspend fun forceRefreshQuestions(): SyncResult = syncMutex.withLock {
        try {
            _syncState.value = SyncStatus.SYNCING
            fetchAndStore()

            val remoteVersion = remote.getSyncVersion()
            if (remoteVersion != null) {
                local.setSyncMeta(KEY_LAST_SYNC_VERSION, remoteVersion.toString())
            }
            local.setSyncMeta(KEY_LAST_SYNC_AT, currentTimeMillis().toString())

            _syncState.value = SyncStatus.IDLE
            SyncResult.UPDATED
        } catch (e: Exception) {
            _syncState.value = SyncStatus.ERROR
            SyncResult.FAILED
        }
    }

    /**
     * Returns the lastSyncAt timestamp (epoch millis) or null if never synced.
     */
    override suspend fun getLastSyncAt(): Long? {
        return local.getSyncMeta(KEY_LAST_SYNC_AT)?.toLongOrNull()
    }

    private suspend fun fetchAndStore(): List<Question> {
        val remoteQuestions = remote.fetchAllActiveQuestions()

        val inserts = remoteQuestions.map { q ->
            QuestionInsert(
                id = q.id,
                text = q.dto.text,
                options = Json.encodeToString(
                    ListSerializer(String.serializer()),
                    q.dto.options,
                ),
                correctAnswer = q.dto.correctAnswer,
                category = q.dto.category,
                level = q.dto.level,
                infotip = q.dto.infotip,
                active = q.dto.active,
                type = q.dto.type,
                updatedAtMillis = q.updatedAtMillis,
            )
        }

        local.replaceAll(inserts)

        return remoteQuestions.map { q ->
            QuestionMapper.toDomain(id = q.id, dto = q.dto)
        }
    }
}

enum class SyncStatus { IDLE, SYNCING, ERROR }
