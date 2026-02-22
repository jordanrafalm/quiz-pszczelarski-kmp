package pl.quizpszczelarski.shared.domain.usecase

import pl.quizpszczelarski.shared.domain.model.UserProfile
import pl.quizpszczelarski.shared.domain.repository.UserRepository

/**
 * Signs in anonymously and ensures user profile exists in Firestore.
 */
class EnsureUserUseCase(
    private val userRepository: UserRepository,
) {

    /**
     * @return Pair(uid, userProfile).
     */
    suspend operator fun invoke(): Pair<String, UserProfile> {
        val uid = userRepository.ensureSignedIn()
        val profile = userRepository.ensureUserProfile(uid)
        return uid to profile
    }
}
