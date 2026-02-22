package pl.quizpszczelarski.shared.domain.usecase

import pl.quizpszczelarski.shared.data.local.PendingScoreDataSource

/**
 * Tries to submit all locally queued scores to Firestore.
 * Deletes each score only after successful remote submit.
 *
 * Uses [currentUid] for submission — after re-auth the anonymous uid may change,
 * so we always submit under the current user, not the uid stored in the record.
 *
 * Called during splash when network is available.
 */
suspend fun flushPendingScores(
    currentUid: String,
    submitScore: SubmitScoreUseCase,
    pendingScoreDataSource: PendingScoreDataSource,
) {
    val pending = pendingScoreDataSource.getAll()
    for (record in pending) {
        try {
            submitScore(currentUid, record.score)
            pendingScoreDataSource.delete(record.id)
        } catch (_: Exception) {
            // Still offline or error — stop trying, remaining scores stay queued
            break
        }
    }
}
