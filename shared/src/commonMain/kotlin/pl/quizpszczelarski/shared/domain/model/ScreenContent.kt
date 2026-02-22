package pl.quizpszczelarski.shared.domain.model

/**
 * Generic screen content holder.
 * Will evolve into quiz-specific models in later phases.
 */
data class ScreenContent(
    val title: String,
    val subtitle: String,
)
