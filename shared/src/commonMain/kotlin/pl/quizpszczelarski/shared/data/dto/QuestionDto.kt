package pl.quizpszczelarski.shared.data.dto

import kotlinx.serialization.Serializable

/**
 * Firestore document shape for the `questions` collection.
 * Field names must match Firestore document fields exactly.
 */
@Serializable
data class QuestionDto(
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: Int = 0,
    val category: String = "",
    val level: String = "",
    val infotip: String = "",
    val active: Boolean = true,
    val type: String = "SINGLE",
)
