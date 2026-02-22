package pl.quizpszczelarski.shared.data.source

import pl.quizpszczelarski.shared.domain.model.Question
import pl.quizpszczelarski.shared.domain.repository.QuestionRepository

/**
 * Hardcoded quiz questions for MVP.
 * Migrated from UI_figma/src/app/data/quizData.ts.
 *
 * Will be replaced by a remote data source (Firebase) in a later phase.
 */
class LocalQuestionDataSource : QuestionRepository {

    override fun getAllQuestions(): List<Question> = questions

    companion object {
        private val questions = listOf(
            Question(
                id = 1,
                text = "Ile oczu ma pszczoła miodna?",
                options = listOf("2 oczy", "5 oczu", "8 oczu", "10 oczu"),
                correctAnswerIndex = 1,
            ),
            Question(
                id = 2,
                text = "Jak nazywa się największa pszczoła w ulu?",
                options = listOf("Robotnica", "Matka pszczela", "Truten", "Strażniczka"),
                correctAnswerIndex = 1,
            ),
            Question(
                id = 3,
                text = "Ile skrzydeł ma pszczoła?",
                options = listOf("2 skrzydła", "4 skrzydła", "6 skrzydeł", "8 skrzydeł"),
                correctAnswerIndex = 1,
            ),
            Question(
                id = 4,
                text = "Co pszczoły wykorzystują do komunikacji w ulu?",
                options = listOf("Dźwięki", "Taniec", "Feromony", "Wszystkie powyższe"),
                correctAnswerIndex = 3,
            ),
            Question(
                id = 5,
                text = "Ile kwiatów pszczoła odwiedza w ciągu jednego lotu?",
                options = listOf("10-20", "50-100", "500-1000", "2000-3000"),
                correctAnswerIndex = 1,
            ),
        )
    }
}
