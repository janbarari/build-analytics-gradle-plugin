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
package io.github.janbarari.gradle.extension

import io.github.janbarari.gradle.analytics.domain.model.ChartPoint
import io.github.janbarari.gradle.analytics.domain.model.TimespanChartPoint
import io.github.janbarari.gradle.utils.DateTimeUtils
import io.github.janbarari.gradle.utils.MathUtils

fun List<TimespanChartPoint>.minimize(targetSize: Int): List<TimespanChartPoint> {
    return if (size > targetSize)
        calculatePointsMean(this).minimize(targetSize)
    else this
}

fun calculatePointsMean(values: List<TimespanChartPoint>): List<TimespanChartPoint> {

    if (values.isEmpty()) return values

    val mean = arrayListOf<TimespanChartPoint>()
    val size = values.size
    var nextIndex = 0

    for (i in values.indices) {
        if (i < nextIndex) continue

        if (i + 1 >= size) {
            mean.add(values[i])
        } else {

            var finishedAt = values[i + 1].to
            if (finishedAt.isNull()) finishedAt = values[i + 1].from

            mean.add(
                TimespanChartPoint(
                    value = MathUtils.longMean(values[i].value, values[i + 1].value),
                    from = values[i].from,
                    to = finishedAt
                )
            )

            nextIndex = i + 2
        }
    }

    return mean
}

fun Collection<TimespanChartPoint>.mapToChartPoints(): List<ChartPoint> {
    return map {
        val period = if (it.to.isNull()) {
            DateTimeUtils.format(it.from, "dd/MM")
        } else {
            DateTimeUtils.format(it.from, "dd/MM") + "-" +
                    DateTimeUtils.format(ensureNotNull(it.to), "dd/MM")
        }
        ChartPoint(it.value, period)
    }
}
