package pl.quizpszczelarski.shared.domain.usecase

import pl.quizpszczelarski.shared.domain.repository.SettingsRepository

class SetHapticsEnabledUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setHapticsEnabled(enabled)
    }
}
