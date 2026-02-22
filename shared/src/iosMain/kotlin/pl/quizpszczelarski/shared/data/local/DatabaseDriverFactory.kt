package pl.quizpszczelarski.shared.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import pl.quizpszczelarski.shared.data.local.db.QuizDatabase

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver {
        return NativeSqliteDriver(
            schema = QuizDatabase.Schema,
            name = "quiz.db",
        )
    }
}
