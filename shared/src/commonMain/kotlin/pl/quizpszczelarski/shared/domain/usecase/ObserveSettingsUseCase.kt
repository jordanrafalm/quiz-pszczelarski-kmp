package pl.quizpszczelarski.shared.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.quizpszczelarski.shared.domain.model.SettingsState
import pl.quizpszczelarski.shared.domain.repository.SettingsRepository

class ObserveSettingsUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<SettingsState> = repository.getSettingsFlow()
}
