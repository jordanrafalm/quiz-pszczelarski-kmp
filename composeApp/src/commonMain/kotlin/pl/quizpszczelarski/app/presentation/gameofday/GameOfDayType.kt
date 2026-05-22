package pl.quizpszczelarski.app.presentation.gameofday

import kotlinx.datetime.LocalDate
import kotlinx.datetime.DayOfWeek

/**
 * Type of game for Game of Day. Changes daily based on LocalDate.
 */
enum class GameOfDayType {
    FlappyBee, Maze, Memory, Sequence;

    companion object {
        fun fromDate(date: LocalDate = LocalDate(2026, 5, 22)): GameOfDayType {
            val dayOfWeek = date.dayOfWeek
            return when (dayOfWeek) {
                DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY -> FlappyBee
                DayOfWeek.TUESDAY -> Maze
                DayOfWeek.THURSDAY -> Memory
                DayOfWeek.FRIDAY -> Sequence
                else -> FlappyBee
            }
        }
    }

    fun title() = when (this) {
        FlappyBee -> "Flappy Bee"
        Maze -> "Pszczeli labirynt"
        Memory -> "Pary pszczelarskie"
        Sequence -> "Sekwencja kwiatów"
    }

    fun description() = when (this) {
        FlappyBee -> "Pomagaj pszczołce lecieć między przeszkodami!"
        Maze -> "Poprowadź pszczołę do celu..."
        Memory -> "Dopasuj pary kart..."
        Sequence -> "Powtórz sekwencję..."
    }

    fun emoji() = when (this) {
        FlappyBee -> "🐝"
        Maze -> "🐝"
        Memory -> "🃏"
        Sequence -> "🌸"
    }
}
