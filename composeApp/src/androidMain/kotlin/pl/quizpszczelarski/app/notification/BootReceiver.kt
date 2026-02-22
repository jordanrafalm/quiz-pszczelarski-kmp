package pl.quizpszczelarski.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pl.quizpszczelarski.app.platform.AndroidNotificationScheduler

/**
 * BroadcastReceiver that fires when the device reboots.
 * Reschedules the quiz reminder alarm if notifications are enabled in settings.
 *
 * Reads `notifications_enabled` directly from SharedPreferences so we don't need
 * the full KMP [SettingsRepository] in this lightweight receiver.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.QUICKBOOT_POWERON"
        ) return

        val prefs = context.getSharedPreferences("quiz_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)

        if (notificationsEnabled) {
            AndroidNotificationScheduler(context).scheduleQuizReminder()
        }
    }
}
