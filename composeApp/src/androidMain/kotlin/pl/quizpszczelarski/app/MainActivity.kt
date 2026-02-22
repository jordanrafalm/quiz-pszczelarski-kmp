package pl.quizpszczelarski.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CompletableDeferred
import pl.quizpszczelarski.app.notification.QuizReminderReceiver
import pl.quizpszczelarski.app.platform.AndroidHaptics
import pl.quizpszczelarski.app.platform.AndroidNotificationScheduler
import pl.quizpszczelarski.app.platform.AndroidSplashSoundPlayer
import pl.quizpszczelarski.shared.data.local.DatabaseDriverFactory
import pl.quizpszczelarski.shared.data.settings.SettingsFactory

class MainActivity : ComponentActivity() {

    /**
     * Deferred that bridges the ActivityResult permission callback to the suspend [requestPermission].
     * Replaced on every new permission request.
     */
    private var permissionDeferred: CompletableDeferred<Boolean>? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        permissionDeferred?.complete(isGranted)
        permissionDeferred = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()

        val scheduler = AndroidNotificationScheduler(
            context = applicationContext,
            permissionRequester = {
                val deferred = CompletableDeferred<Boolean>()
                permissionDeferred = deferred
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                deferred.await()
            },
        )

        setContent {
            App(
                driverFactory = DatabaseDriverFactory(applicationContext),
                settingsFactory = SettingsFactory(applicationContext),
                haptics = AndroidHaptics(applicationContext),
                splashSoundPlayer = AndroidSplashSoundPlayer(cacheDir),
                notificationScheduler = scheduler,
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                QuizReminderReceiver.CHANNEL_ID,
                "Przypomnienia o quizie",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Cykliczne przypomnienia o rozegraniu quizu pszczelarskiego"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
