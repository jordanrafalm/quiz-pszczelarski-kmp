package pl.quizpszczelarski.app.platform

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import pl.quizpszczelarski.app.notification.QuizReminderReceiver
import java.util.Calendar

/**
 * Android implementation of [NotificationScheduler].
 *
 * Uses [AlarmManager.setInexactRepeating] to fire every 48 hours starting at the next 18:00
 * that is at least 24 hours from now.
 *
 * @param context Application context.
 * @param permissionRequester Suspend lambda provided by [MainActivity] that launches the system
 *   POST_NOTIFICATIONS permission dialog and returns whether the user granted it.
 */
class AndroidNotificationScheduler(
    private val context: Context,
    private val permissionRequester: suspend () -> Boolean = { false },
) : NotificationScheduler {

    override suspend fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        if (isPermissionGranted()) return true
        return permissionRequester()
    }

    override fun isPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun scheduleQuizReminder() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent() ?: return

        // Calculate next 18:00 that is >= 24h from now
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = now + AlarmManager.INTERVAL_DAY
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val triggerAt = calendar.timeInMillis

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            AlarmManager.INTERVAL_DAY * 2,
            pendingIntent,
        )
    }

    override fun cancelQuizReminder() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        buildPendingIntent()?.let { alarmManager.cancel(it) }
    }

    private fun buildPendingIntent(): PendingIntent? {
        val intent = Intent(context, QuizReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}
