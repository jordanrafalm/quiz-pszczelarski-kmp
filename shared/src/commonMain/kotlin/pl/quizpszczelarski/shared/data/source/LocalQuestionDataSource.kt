package pl.quizpszczelarski.shared.data.source

import pl.quizpszczelarski.shared.domain.model.Question
import pl.quizpszczelarski.shared.domain.repository.QuestionRepository

/**
 * Hardcoded quiz questions for fallback / offline.
 * Migrated from UI_figma/src/app/data/quizData.ts.
 *
 * Implements [QuestionRepository] as a local data source.
 *
 * Note: [level] and [category] filters are intentionally ignored —
 * local questions do not have category/level metadata.
 */
class LocalQuestionDataSource : QuestionRepository {

    /**
     * Returns local questions ignoring [level] and [category] filters.
     * Only [limit] is applied.
     */
    override suspend fun getActiveQuestions(
        level: String?,
        category: String?,
        limit: Int,
    ): List<Question> = questions.take(limit)

    companion object {
        private val questions = listOf(
            Question(
                id = "local-1",
                text = "Ile oczu ma pszczoła miodna?",
                options = listOf("2 oczy", "5 oczu", "8 oczu", "10 oczu"),
                correctAnswerIndex = 1,
            ),
            Question(
                id = "local-2",
                text = "Jak nazywa się największa pszczoła w ulu?",
                options = listOf("Robotnica", "Matka pszczela", "Truten", "Strażniczka"),
                correctAnswerIndex = 1,
            ),
            Question(
                id = "local-3",
                text = "Ile skrzydeł ma pszczoła?",
                options = listOf("2 skrzydła", "4 skrzydła", "6 skrzydeł", "8 skrzydeł"),
                correctAnswerIndex = 1,
            ),
            Question(
                id = "local-4",
                text = "Co pszczoły wykorzystują do komunikacji w ulu?",
                options = listOf("Dźwięki", "Taniec", "Feromony", "Wszystkie powyższe"),
                correctAnswerIndex = 3,
            ),
            Question(
                id = "local-5",
                text = "Ile kwiatów pszczoła odwiedza w ciągu jednego lotu?",
                options = listOf("10-20", "50-100", "500-1000", "2000-3000"),
                correctAnswerIndex = 1,
            ),
        )
    }
}
