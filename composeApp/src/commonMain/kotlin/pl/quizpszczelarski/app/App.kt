package pl.quizpszczelarski.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import pl.quizpszczelarski.app.ui.GoldScreen
import pl.quizpszczelarski.shared.presentation.GoldScreenViewModel

@Composable
fun App() {
    val viewModel = remember { GoldScreenViewModel() }
    GoldScreen(viewModel = viewModel)
}
