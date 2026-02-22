package pl.quizpszczelarski.app.platform

import androidx.compose.runtime.compositionLocalOf
import pl.quizpszczelarski.shared.domain.model.SettingsState

/**
 * CompositionLocal providing the current [SettingsState].
 * Updated from AppNavigation by collecting the SettingsRepository flow.
 */
val LocalSettingsState = compositionLocalOf { SettingsState() }
