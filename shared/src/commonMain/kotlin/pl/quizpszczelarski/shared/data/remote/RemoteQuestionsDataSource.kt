package pl.quizpszczelarski.shared.data.remote

import dev.gitlive.firebase.firestore.FirebaseFirestore
import pl.quizpszczelarski.shared.data.dto.QuestionDto

/**
 * Reads questions + sync metadata from Firestore.
 * No caching logic — pure remote reads.
 */
interface RemoteQuestionsDataSource {

    /**
     * Reads the current sync version from `questionsMeta/snapshot`.
     * Returns null if the document doesn't exist.
     */
    suspend fun getSyncVersion(): Int?

    /**
     * Fetches all active questions with their updatedAt timestamps.
     */
    suspend fun fetchAllActiveQuestions(): List<QuestionWithMeta>
}

/**
 * Firestore implementation of [RemoteQuestionsDataSource].
 */
class FirestoreQuestionsDataSource(
    private val firestore: FirebaseFirestore,
) : RemoteQuestionsDataSource {

    override suspend fun getSyncVersion(): Int? {
        return try {
            val doc = firestore.collection("questionsMeta").document("snapshot").get()
            if (!doc.exists) return null
            doc.get<Int>("version")
        } catch (_: Exception) {
            // Document doesn't exist, permission denied, or field missing.
            // Treat as "no meta doc" — not as network failure.
            null
        }
    }

    override suspend fun fetchAllActiveQuestions(): List<QuestionWithMeta> {
        val snapshot = firestore.collection("questions")
            .where { "active" equalTo true }
            .get()

        return snapshot.documents.map { doc ->
            val dto = doc.data<QuestionDto>()
            val updatedAtMillis = try {
                doc.get<Long>("updatedAt")
            } catch (_: Exception) {
                0L
            }
            QuestionWithMeta(
                id = doc.id,
                dto = dto,
                updatedAtMillis = updatedAtMillis,
            )
        }
    }
}
