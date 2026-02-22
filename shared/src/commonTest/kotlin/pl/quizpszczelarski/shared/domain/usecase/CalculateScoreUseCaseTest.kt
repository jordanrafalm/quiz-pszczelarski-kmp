package pl.quizpszczelarski.shared.domain.usecase

import pl.quizpszczelarski.shared.domain.model.Question
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateScoreUseCaseTest {

    private val useCase = CalculateScoreUseCase()

    private val sampleQuestions = listOf(
        Question(id = "1", text = "Q1", options = listOf("A", "B", "C", "D"), correctAnswerIndex = 0),
        Question(id = "2", text = "Q2", options = listOf("A", "B", "C", "D"), correctAnswerIndex = 1),
        Question(id = "3", text = "Q3", options = listOf("A", "B", "C", "D"), correctAnswerIndex = 2),
        Question(id = "4", text = "Q4", options = listOf("A", "B", "C", "D"), correctAnswerIndex = 3),
        Question(id = "5", text = "Q5", options = listOf("A", "B", "C", "D"), correctAnswerIndex = 0),
    )

    @Test
    fun `should return perfect score when all answers correct`() {
        val answers = listOf(0, 1, 2, 3, 0)
        val result = useCase(sampleQuestions, answers)

        assertEquals(5, result.score)
        assertEquals(5, result.totalQuestions)
        assertEquals(100, result.percentage)
    }

    @Test
    fun `should return zero score when all answers wrong`() {
        val answers = listOf(3, 3, 3, 0, 3)
        val result = useCase(sampleQuestions, answers)

        assertEquals(0, result.score)
        assertEquals(5, result.totalQuestions)
        assertEquals(0, result.percentage)
    }

    @Test
    fun `should return partial score when some answers correct`() {
        val answers = listOf(0, 1, 0, 0, 0) // 3 correct: Q1, Q2, Q5
        val result = useCase(sampleQuestions, answers)

        assertEquals(3, result.score)
        assertEquals(5, result.totalQuestions)
        assertEquals(60, result.percentage)
    }

    @Test
    fun `should handle empty quiz`() {
        val result = useCase(emptyList(), emptyList())

        assertEquals(0, result.score)
        assertEquals(0, result.totalQuestions)
        assertEquals(0, result.percentage)
    }
}
