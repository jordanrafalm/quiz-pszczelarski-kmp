package pl.quizpszczelarski.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pl.quizpszczelarski.app.platform.AndroidHaptics
import pl.quizpszczelarski.app.platform.AndroidSplashSoundPlayer
import pl.quizpszczelarski.shared.data.local.DatabaseDriverFactory
import pl.quizpszczelarski.shared.data.settings.SettingsFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(
                driverFactory = DatabaseDriverFactory(applicationContext),
                settingsFactory = SettingsFactory(applicationContext),
                haptics = AndroidHaptics(applicationContext),
                splashSoundPlayer = AndroidSplashSoundPlayer(cacheDir),
            )
        }
    }
}
