/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
object DateTimeUtils {

    const val ONE_SECOND_IN_MILLIS = 1000
    const val ONE_DAY_IN_MILLIS = 86_400_000
    val DEFAULT_ZONE: ZoneId = ZoneId.of("UTC")

    fun getDayStartMs(): Long {
        return LocalDate.now().atStartOfDay(DEFAULT_ZONE).toEpochSecond() * ONE_SECOND_IN_MILLIS
    }

    fun getDayEndMs(): Long {
        return getDayStartMs() + ONE_DAY_IN_MILLIS
    }

    fun calculateDayInPastMonthsMs(from: Long, months: Long): Long {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(from), DEFAULT_ZONE)
            .minusMonths(months)
            .atZone(DEFAULT_ZONE)
            .toEpochSecond() * ONE_SECOND_IN_MILLIS
    }

    fun msToDateString(timeInMs: Long): String {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeInMs), DEFAULT_ZONE)
            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    }

    fun msToDateTimeString(timeInMs: Long): String {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeInMs), DEFAULT_ZONE)
            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm a 'UTC'"))
    }

}
