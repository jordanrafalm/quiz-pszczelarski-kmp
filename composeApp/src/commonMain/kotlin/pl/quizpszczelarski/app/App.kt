package pl.quizpszczelarski.app

import androidx.compose.runtime.Composable
import pl.quizpszczelarski.app.navigation.AppNavigation
import pl.quizpszczelarski.app.ui.theme.AppTheme
import pl.quizpszczelarski.shared.data.local.DatabaseDriverFactory

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    AppTheme {
        AppNavigation(driverFactory)
    }
}
