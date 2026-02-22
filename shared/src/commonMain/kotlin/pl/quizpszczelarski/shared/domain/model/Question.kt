package pl.quizpszczelarski.shared.domain.model

/**
 * Quiz question entity.
 *
 * @param id Unique identifier.
 * @param text Question text displayed to the user.
 * @param options List of answer options.
 * @param correctAnswerIndex 0-based index of the correct answer in [options].
 */
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
)
