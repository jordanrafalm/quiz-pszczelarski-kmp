package pl.quizpszczelarski.shared.data.dto

import kotlinx.serialization.Serializable

/**
 * Firestore document shape for `users/{uid}`.
 * Timestamps are handled via Firestore ServerTimestamp, not serialized here.
 */
@Serializable
data class UserDto(
    val nickname: String = "",
    val totalScore: Int = 0,
    val gamesPlayed: Int = 0,
)
