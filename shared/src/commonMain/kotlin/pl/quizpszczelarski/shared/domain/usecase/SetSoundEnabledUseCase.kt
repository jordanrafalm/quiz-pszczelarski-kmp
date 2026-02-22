package pl.quizpszczelarski.shared.domain.usecase

import pl.quizpszczelarski.shared.domain.repository.SettingsRepository

class SetSoundEnabledUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setSoundEnabled(enabled)
    }
}
