package pl.quizpszczelarski.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pl.quizpszczelarski.app.presentation.quiz.QuizState
import pl.quizpszczelarski.app.ui.theme.AppTheme
import pl.quizpszczelarski.shared.domain.model.Question

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    AppTheme {
        QuizScreen(
            state = QuizState(
                questions = listOf(
                    Question(
                        id = "1",
                        text = "Ile oczu ma pszczoła?",
                        options = listOf("2 oczy", "5 oczu", "8 oczu", "10 oczu"),
                        correctAnswerIndex = 1,
                        category = "biologia",
                        level = "easy",
                        infotip = "Pszczoła ma 5 oczu: 2 duże oczy złożone i 3 małe przyoczka.",
                    ),
                    Question(
                        id = "2",
                        text = "Jak długo żyje pszczoła robotnica?",
                        options = listOf("2 tygodnie", "6 tygodni", "3 miesiące", "1 rok"),
                        correctAnswerIndex = 1,
                    ),
                ),
                currentQuestionIndex = 0,
                selectedAnswerIndex = null,
                score = 0,
                isLoading = false,
                isRefreshing = false,
                isOffline = false,
            ),
            onIntent = {},
        )
    }
}
