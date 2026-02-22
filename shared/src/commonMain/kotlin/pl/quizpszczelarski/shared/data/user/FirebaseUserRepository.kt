package pl.quizpszczelarski.shared.data.user

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import pl.quizpszczelarski.shared.data.dto.UserDto
import pl.quizpszczelarski.shared.data.mapper.UserMapper
import pl.quizpszczelarski.shared.domain.model.UserProfile
import pl.quizpszczelarski.shared.domain.repository.UserRepository
import kotlin.random.Random

/**
 * Firebase Auth (anonymous) + Firestore user profile repository.
 */
class FirebaseUserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : UserRepository {

    override suspend fun ensureSignedIn(): String {
        val currentUser = auth.currentUser
        if (currentUser != null) return currentUser.uid

        val result = auth.signInAnonymously()
        return result.user?.uid
            ?: throw IllegalStateException("Anonymous sign-in returned null user")
    }

    override suspend fun ensureUserProfile(uid: String): UserProfile {
        val docRef = firestore.collection("users").document(uid)
        val snapshot = docRef.get()

        if (snapshot.exists) {
            // Update lastSeenAt
            docRef.update("lastSeenAt" to FieldValue.serverTimestamp)
            val dto = snapshot.data<UserDto>()
            return UserMapper.toDomain(uid, dto)
        }

        // Create new profile
        val nickname = generateNickname()
        val newUser = mapOf(
            "nickname" to nickname,
            "totalScore" to 0,
            "gamesPlayed" to 0,
            "createdAt" to FieldValue.serverTimestamp,
            "lastSeenAt" to FieldValue.serverTimestamp,
            "lastGameAt" to FieldValue.serverTimestamp,
            "lastGameScore" to 0,
        )
        docRef.set(newUser)

        return UserProfile(
            uid = uid,
            nickname = nickname,
            totalScore = 0,
            gamesPlayed = 0,
        )
    }

    override suspend fun addScore(uid: String, delta: Int) {
        val docRef = firestore.collection("users").document(uid)
        docRef.update(
            "totalScore" to FieldValue.increment(delta),
            "gamesPlayed" to FieldValue.increment(1),
            "lastGameAt" to FieldValue.serverTimestamp,
            "lastGameScore" to delta,
        )
    }

    private fun generateNickname(): String {
        val suffix = Random.nextInt(1000, 9999)
        return "Pszczelarz#$suffix"
    }
}
