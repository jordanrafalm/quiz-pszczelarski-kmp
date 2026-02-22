package pl.quizpszczelarski.shared.presentation

/**
 * UI state for the Gold Screen.
 * Immutable data class — single source of truth for what UI renders.
 */
data class GoldScreenUiState(
    val title: String = "",
    val subtitle: String = "",
)
