package pl.quizpszczelarski.shared.domain.model

/**
 * Phases for Game of Day notifications.
 * Controls notification frequency based on time since first update.
 */
enum class NotificationPhase {
    /** Initial phase: 2 days after app update. */
    INITIAL,
    
    /** Regular phase: every 2 days (for the first 2 weeks). */
    REGULAR,
    
    /** Mature phase: every 2 weeks (after 2 weeks of regular phase). */
    MATURE,
}
