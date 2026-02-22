package pl.quizpszczelarski.shared.data.leaderboard

import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.quizpszczelarski.shared.data.dto.UserDto
import pl.quizpszczelarski.shared.data.mapper.UserMapper
import pl.quizpszczelarski.shared.domain.model.LeaderboardEntry
import pl.quizpszczelarski.shared.domain.repository.LeaderboardRepository

/**
 * Observes Firestore `users` collection ordered by totalScore descending.
 * Provides real-time leaderboard updates via Flow.
 */
class FirebaseLeaderboardRepository(
    private val firestore: FirebaseFirestore,
) : LeaderboardRepository {

    override fun observeTopUsers(limit: Int, currentUid: String): Flow<List<LeaderboardEntry>> {
        return firestore.collection("users")
            .orderBy("totalScore", Direction.DESCENDING)
            .limit(limit)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapIndexed { index, doc ->
                    val dto = doc.data<UserDto>()
                    UserMapper.toLeaderboardEntry(
                        uid = doc.id,
                        dto = dto,
                        rank = index + 1,
                        currentUid = currentUid,
                    )
                }
            }
    }
}
