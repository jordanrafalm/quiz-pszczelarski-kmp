package pl.quizpszczelarski.shared.domain.model

/**
 * User profile domain model.
 *
 * @param uid Firebase Auth UID.
 * @param nickname Display name (e.g. "Pszczelarz#4821").
 * @param totalScore Cumulative points across all quizzes.
 * @param gamesPlayed Total number of completed quizzes.
 */
data class UserProfile(
    val uid: String,
    val nickname: String,
    val totalScore: Int,
    val gamesPlayed: Int,
)
