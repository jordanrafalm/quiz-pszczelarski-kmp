package pl.quizpszczelarski.shared.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import pl.quizpszczelarski.shared.data.local.db.QuizDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun create(): SqlDriver {
        return AndroidSqliteDriver(
            schema = QuizDatabase.Schema,
            context = context,
            name = "quiz.db",
        )
    }
}
