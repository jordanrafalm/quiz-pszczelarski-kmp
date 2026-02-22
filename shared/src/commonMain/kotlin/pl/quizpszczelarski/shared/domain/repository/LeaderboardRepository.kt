package pl.quizpszczelarski.shared.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.quizpszczelarski.shared.domain.model.LeaderboardEntry

/**
 * Contract for observing leaderboard data.
 * Implemented via Firestore real-time listener in the data layer.
 */
interface LeaderboardRepository {

    /**
     * Observes top users ordered by totalScore descending.
     * Emits new list on every Firestore snapshot change.
     *
     * @param currentUid UID of the current user (used to mark [LeaderboardEntry.isCurrentUser]).
     */
    fun observeTopUsers(limit: Int = 50, currentUid: String = ""): Flow<List<LeaderboardEntry>>
}
