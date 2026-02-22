package pl.quizpszczelarski.shared.domain.model

/**
 * Domain-friendly error types.
 * Used to map Firebase/network errors to presentation-layer messages.
 */
sealed interface AppError {
    data class Network(val message: String? = null) : AppError
    data class NotFound(val entity: String) : AppError
    data class Unknown(val message: String? = null, val cause: Throwable? = null) : AppError
}
