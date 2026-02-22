package pl.quizpszczelarski.shared.data.mapper

import pl.quizpszczelarski.shared.data.dto.UserDto
import pl.quizpszczelarski.shared.domain.model.LeaderboardEntry
import pl.quizpszczelarski.shared.domain.model.UserProfile

/**
 * Maps Firestore [UserDto] to domain models.
 */
object UserMapper {

    fun toDomain(uid: String, dto: UserDto): UserProfile {
        return UserProfile(
            uid = uid,
            nickname = dto.nickname,
            totalScore = dto.totalScore,
            gamesPlayed = dto.gamesPlayed,
        )
    }

    fun toLeaderboardEntry(
        uid: String,
        dto: UserDto,
        rank: Int,
        currentUid: String,
    ): LeaderboardEntry {
        return LeaderboardEntry(
            uid = uid,
            rank = rank,
            name = dto.nickname,
            score = dto.totalScore,
            isCurrentUser = uid == currentUid,
        )
    }
}
