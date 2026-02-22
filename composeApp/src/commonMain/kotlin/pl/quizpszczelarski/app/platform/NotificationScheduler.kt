package pl.quizpszczelarski.app.platform

/**
 * Platform-agnostic interface for scheduling/canceling quiz reminder notifications.
 *
 * Implementations:
 * - Android: AlarmManager, repeating every 48 h, initial trigger at next 18:00
 * - iOS: UNUserNotificationCenter, 64 pre-scheduled triggers (every 2 days at 18:00)
 */
interface NotificationScheduler {
    /** Request the OS permission to post notifications. Returns true if granted. */
    suspend fun requestPermission(): Boolean

    /** Returns true if notification permission is currently granted. */
    fun isPermissionGranted(): Boolean

    /** Schedule recurring quiz reminders (every 2 days at 18:00). */
    fun scheduleQuizReminder()

    /** Cancel all pending quiz reminder notifications. */
    fun cancelQuizReminder()
}

/** No-op implementation used as a fallback. */
val NoOpNotificationScheduler: NotificationScheduler = object : NotificationScheduler {
    override suspend fun requestPermission(): Boolean = false
    override fun isPermissionGranted(): Boolean = false
    override fun scheduleQuizReminder() = Unit
    override fun cancelQuizReminder() = Unit
}
