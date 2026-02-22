package pl.quizpszczelarski.app.platform

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSUserDefaults
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

/**
 * iOS implementation of [NotificationScheduler].
 *
 * Uses [UNUserNotificationCenter] to schedule 64 one-off notifications,
 * one for every other day at 18:00 (covering the next 128 days).
 * The batch is refreshed on each app launch.
 */
class IosNotificationScheduler : NotificationScheduler {

    companion object {
        private const val KEY_PERMISSION_GRANTED = "ios_notification_permission_granted"
    }

    /**
     * Persists permission state in NSUserDefaults so [isPermissionGranted] returns
     * the correct value even after an app restart (in-memory flag would reset to false).
     */
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override suspend fun requestPermission(): Boolean {
        val granted = suspendCancellableCoroutine<Boolean> { continuation ->
            UNUserNotificationCenter.currentNotificationCenter()
                .requestAuthorizationWithOptions(
                    UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
                ) { granted, _ ->
                    continuation.resume(granted)
                }
        }
        userDefaults.setBool(granted, KEY_PERMISSION_GRANTED)
        return granted
    }

    override fun isPermissionGranted(): Boolean =
        userDefaults.boolForKey(KEY_PERMISSION_GRANTED)

    override fun scheduleQuizReminder() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removeAllPendingNotificationRequests()

        val content = UNMutableNotificationContent().apply {
            setTitle("🐝 Quiz Pszczelarski")
            setBody("Dowiedz się więcej o pszczołach! Rozegraj quiz.")
            setSound(platform.UserNotifications.UNNotificationSound.defaultSound())
        }

        val calendar = NSCalendar.currentCalendar
        val now = NSDate()

        for (i in 0 until 64) {
            val daysOffset = (i * 2 + 1).toLong()
            val targetDate = calendar.dateByAddingUnit(
                NSCalendarUnitDay,
                daysOffset,
                now,
                0u,
            ) ?: continue

            val components = calendar.components(
                NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
                fromDate = targetDate,
            )
            components.hour = 18
            components.minute = 0
            components.second = 0

            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                components,
                repeats = false,
            )

            val request = UNNotificationRequest.requestWithIdentifier(
                "quiz_reminder_$i",
                content,
                trigger,
            )

            center.addNotificationRequest(request) { _ -> }
        }
    }

    override fun cancelQuizReminder() {
        UNUserNotificationCenter.currentNotificationCenter()
            .removeAllPendingNotificationRequests()
    }
}
