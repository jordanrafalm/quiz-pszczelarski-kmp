package pl.quizpszczelarski.shared.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.quizpszczelarski.shared.domain.model.ScreenContent

/**
 * Shared ViewModel for the Gold Screen.
 * Holds UI state as StateFlow — consumed by Compose UI in composeApp.
 */
class GoldScreenViewModel {

    private val _uiState = MutableStateFlow(GoldScreenUiState())
    val uiState: StateFlow<GoldScreenUiState> = _uiState.asStateFlow()

    init {
        // Placeholder: in future, a use case will provide this data
        val content = ScreenContent(
            title = "Quiz Pszczelarski",
            subtitle = "Hello from shared!",
        )
        _uiState.value = GoldScreenUiState(
            title = content.title,
            subtitle = content.subtitle,
        )
    }
}
