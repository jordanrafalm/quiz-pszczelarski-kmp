package pl.quizpszczelarski.shared.domain.repository

/**
 * Contract for background question synchronisation.
 *
 * Returned by the data layer, consumed by presentation (QuizViewModel).
 * Keeps ViewModels decoupled from concrete cache / repository implementations.
 */
interface QuestionSyncService {

    /**
     * Checks remote meta document version and syncs if needed.
     * Safe to call from UI coroutines — handles errors internally.
     *
     * @return [SyncResult] indicating what happened.
     */
    suspend fun syncQuestionsIfNeeded(): SyncResult

    /**
     * Returns the lastSyncAt timestamp (epoch millis) or null if never synced.
     */
    suspend fun getLastSyncAt(): Long?
}

/** Outcome of a sync attempt. */
enum class SyncResult {
    /** Local cache matches remote version — no download needed. */
    UP_TO_DATE,
    /** New questions downloaded and stored locally. */
    UPDATED,
    /** Remote meta document doesn't exist — nothing to compare. */
    NO_META_DOC,
    /** Sync failed (network error, timeout, etc.). */
    FAILED,
}
