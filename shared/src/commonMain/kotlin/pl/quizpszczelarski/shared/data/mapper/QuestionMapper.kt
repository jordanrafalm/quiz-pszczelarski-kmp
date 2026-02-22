package pl.quizpszczelarski.shared.data.mapper

import pl.quizpszczelarski.shared.data.dto.QuestionDto
import pl.quizpszczelarski.shared.domain.model.Question

/**
 * Maps Firestore [QuestionDto] to domain [Question].
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
}
