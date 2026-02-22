package pl.quizpszczelarski.shared.data.mapper

import pl.quizpszczelarski.shared.data.dto.QuestionDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuestionMapperTest {

    @Test
    fun toDomain_mapsAllFieldsCorrectly() {
        val dto = QuestionDto(
            text = "Ile oczu ma pszczoła?",
            options = listOf("2", "5", "8", "10"),
            correctAnswer = 1,
            category = "biologia",
            level = "podstawowy",
            infotip = "Pszczoła ma 5 oczu",
            active = true,
            type = "SINGLE",
        )

        val result = QuestionMapper.toDomain(id = "q1", dto = dto)

        assertEquals("q1", result.id)
        assertEquals("Ile oczu ma pszczoła?", result.text)
        assertEquals(listOf("2", "5", "8", "10"), result.options)
        assertEquals(1, result.correctAnswerIndex)
        assertEquals("biologia", result.category)
        assertEquals("podstawowy", result.level)
        assertEquals("Pszczoła ma 5 oczu", result.infotip)
    }

    @Test
    fun toDomain_handlesDefaultValues() {
        val dto = QuestionDto(
            text = "Test?",
            options = listOf("A", "B"),
            correctAnswer = 0,
        )

        val result = QuestionMapper.toDomain(id = "q2", dto = dto)

        assertEquals("", result.category)
        assertEquals("", result.level)
        assertEquals("", result.infotip)
    }

    @Test
    fun toDomain_handlesEmptyOptions() {
        val dto = QuestionDto(
            text = "Test?",
            options = emptyList(),
            correctAnswer = 0,
        )

        val result = QuestionMapper.toDomain(id = "q3", dto = dto)

        assertTrue(result.options.isEmpty())
    }
}
