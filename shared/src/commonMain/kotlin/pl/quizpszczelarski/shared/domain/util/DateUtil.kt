@file:OptIn(kotlin.time.ExperimentalTime::class)

package pl.quizpszczelarski.shared.domain.util

import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Returns today's [LocalDate] using the system clock.
 *
 * Uses [kotlin.time.Clock] (available since Kotlin 2.x stdlib) which resolves correctly
 * regardless of which kotlinx-datetime version is on the linker's classpath.
 */
fun todayLocalDate(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
