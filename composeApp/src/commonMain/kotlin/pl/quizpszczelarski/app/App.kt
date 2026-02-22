package pl.quizpszczelarski.app

import androidx.compose.runtime.Composable
import pl.quizpszczelarski.app.navigation.AppNavigation
import pl.quizpszczelarski.app.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        AppNavigation()
    }
}
