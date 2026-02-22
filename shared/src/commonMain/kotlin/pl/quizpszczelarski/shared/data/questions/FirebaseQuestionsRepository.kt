package pl.quizpszczelarski.shared.data.questions

import dev.gitlive.firebase.firestore.FirebaseFirestore
import pl.quizpszczelarski.shared.data.dto.QuestionDto
import pl.quizpszczelarski.shared.data.mapper.QuestionMapper
import pl.quizpszczelarski.shared.domain.model.Question
import pl.quizpszczelarski.shared.domain.repository.QuestionRepository

/**
 * Fetches active questions from Firestore `questions` collection.
 */
class FirebaseQuestionsRepository(
    private val firestore: FirebaseFirestore,
) : QuestionRepository {

    override suspend fun getActiveQuestions(
        level: String?,
        category: String?,
        limit: Int,
    ): List<Question> {
        var query = firestore.collection("questions")
            .where { "active" equalTo true }

        if (level != null) {
            query = query.where { "level" equalTo level }
        }
        if (category != null) {
            query = query.where { "category" equalTo category }
        }

        val snapshot = query.limit(limit).get()

        return snapshot.documents.map { doc ->
            val dto = doc.data<QuestionDto>()
            QuestionMapper.toDomain(id = doc.id, dto = dto)
        }
    }
}
