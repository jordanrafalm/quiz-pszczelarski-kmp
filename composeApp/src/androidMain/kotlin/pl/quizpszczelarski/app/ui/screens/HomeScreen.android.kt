package pl.quizpszczelarski.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pl.quizpszczelarski.app.presentation.home.HomeState
import pl.quizpszczelarski.app.ui.theme.AppTheme

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(
            state = HomeState(
                appTitle = "Quiz Pszczelarski",
                appDescription = "Sprawdź swoją wiedzę o pszczołach",
                showLevelSelect = false,
                selectedQuestionCount = 10,
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenLevelSelectPreview() {
    AppTheme {
        HomeScreen(
            state = HomeState(
                appTitle = "Quiz Pszczelarski",
                appDescription = "Sprawdź swoją wiedzę o pszczołach",
                showLevelSelect = true,
                selectedQuestionCount = 15,
            ),
            onIntent = {},
        )
    }
}
