package pl.quizpszczelarski.shared.domain.repository

import pl.quizpszczelarski.shared.domain.model.UserProfile

/**
 * Contract for user authentication and profile management.
 * Implemented via Firebase Auth + Firestore in the data layer.
 */
interface UserRepository {

    /**
     * Signs in anonymously if not already signed in.
     * @return Firebase UID.
     */
    suspend fun ensureSignedIn(): String

    /**
     * Creates user profile in Firestore if it doesn't exist.
     * Generates nickname like "Pszczelarz#4821".
     * Updates lastSeenAt on every call.
     * @return UserProfile.
     */
    suspend fun ensureUserProfile(uid: String): UserProfile

    /**
     * Atomically increments totalScore and gamesPlayed.
     * Sets lastGameAt and lastGameScore.
     */
    suspend fun addScore(uid: String, delta: Int)

    /**
     * Updates the user's display nickname in Firestore.
     */
    suspend fun updateNickname(uid: String, newNickname: String)
}
