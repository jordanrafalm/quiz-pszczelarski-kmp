package pl.quizpszczelarski.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pl.quizpszczelarski.app.presentation.result.ResultState
import pl.quizpszczelarski.app.ui.theme.AppTheme

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    AppTheme {
        ResultScreen(
            state = ResultState(
                score = 8,
                totalQuestions = 10,
                showNicknamePrompt = false,
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenWithNicknamePreview() {
    AppTheme {
        ResultScreen(
            state = ResultState(
                score = 10,
                totalQuestions = 10,
                showNicknamePrompt = true,
                nicknameInput = "PszczelarzPro",
            ),
            onIntent = {},
        )
    }
}
