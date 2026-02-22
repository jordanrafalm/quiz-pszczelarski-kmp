package pl.quizpszczelarski.shared.domain.model

/**
 * Single leaderboard ranking entry.
 *
 * @param rank Position in the leaderboard (1-based).
 * @param name Player display name.
 * @param score Player score.
 * @param isCurrentUser Whether this entry represents the current user.
 */
data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val score: Int,
    val isCurrentUser: Boolean = false,
)
