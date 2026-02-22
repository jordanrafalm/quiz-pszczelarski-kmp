package pl.quizpszczelarski.shared.domain.usecase

import pl.quizpszczelarski.shared.domain.repository.UserRepository

/**
 * Submits quiz score: increments totalScore + gamesPlayed in Firestore.
 */
class SubmitScoreUseCase(
    private val userRepository: UserRepository,
) {

    /**
     * @param uid User UID.
     * @param score Points earned in this quiz.
     */
    suspend operator fun invoke(uid: String, score: Int) {
        userRepository.addScore(uid, score)
    }
}
