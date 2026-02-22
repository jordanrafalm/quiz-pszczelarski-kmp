package pl.quizpszczelarski.shared.data.mapper

import kotlinx.serialization.json.Json
import pl.quizpszczelarski.shared.data.dto.QuestionDto
import pl.quizpszczelarski.shared.data.local.db.QuestionEntity
import pl.quizpszczelarski.shared.domain.model.Question

/**
 * Maps Firestore [QuestionDto] and DB [QuestionEntity] to domain [Question].
 */
object QuestionMapper {

    fun toDomain(id: String, dto: QuestionDto): Question {
        return Question(
            id = id,
            text = dto.text,
            options = dto.options,
            correctAnswerIndex = dto.correctAnswer,
            category = dto.category,
            level = dto.level,
            infotip = dto.infotip,
        )
    }

    /**
     * Maps SQLDelight [QuestionEntity] to domain [Question].
     * Options are stored as a JSON array string and deserialized here.
     */
    fun entityToDomain(entity: QuestionEntity): Question {
        return Question(
            id = entity.id,
            text = entity.text,
            options = Json.decodeFromString<List<String>>(entity.options),
            correctAnswerIndex = entity.correctAnswer.toInt(),
            category = entity.category,
            level = entity.level,
            infotip = entity.infotip,
        )
    }
}
