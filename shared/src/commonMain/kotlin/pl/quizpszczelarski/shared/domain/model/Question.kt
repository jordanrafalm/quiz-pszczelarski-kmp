package pl.quizpszczelarski.shared.domain.model

/**
 * Quiz question entity.
 *
 * @param id Unique identifier (Firestore document ID).
 * @param text Question text displayed to the user.
 * @param options List of answer options.
 * @param correctAnswerIndex 0-based index of the correct answer in [options].
 * @param category Question category (e.g. "biologia").
 * @param level Difficulty level (e.g. "podstawowy").
 * @param infotip Explanation text shown after answering.
 */
data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val category: String = "",
    val level: String = "",
    val infotip: String = "",
)
