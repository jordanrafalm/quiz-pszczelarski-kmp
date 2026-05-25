package pl.quizpszczelarski.app.platform

/**
 * Platform-agnostic interface for scheduling/canceling reminder notifications.
 *
 * Implementations:
 * - Android: AlarmManager, quiz every 14 days, game-of-day every 2 days at 18:00
 * - iOS: UNUserNotificationCenter, pre-scheduled batch of triggers at 18:00
 */
interface NotificationScheduler {
    /** Request the OS permission to post notifications. Returns true if granted. */
    suspend fun requestPermission(): Boolean

    /** Returns true if notification permission is currently granted. */
    fun isPermissionGranted(): Boolean

    /** Schedule recurring quiz reminders (every 14 days at 18:00). */
    fun scheduleQuizReminder()

    /** Cancel all pending quiz reminder notifications. */
    fun cancelQuizReminder()

    /** Schedule recurring Game of Day reminders (every 2 days at 18:00). */
    fun scheduleGameOfDayReminder()

    /** Cancel all pending Game of Day reminder notifications. */
    fun cancelGameOfDayReminder()
}

/** No-op implementation used as a fallback. */
val NoOpNotificationScheduler: NotificationScheduler = object : NotificationScheduler {
    override suspend fun requestPermission(): Boolean = false
    override fun isPermissionGranted(): Boolean = false
    override fun scheduleQuizReminder() = Unit
    override fun cancelQuizReminder() = Unit
    override fun scheduleGameOfDayReminder() = Unit
    override fun cancelGameOfDayReminder() = Unit
}
