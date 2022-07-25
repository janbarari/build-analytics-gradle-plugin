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
package io.github.janbarari.gradle.analytics.metric.totalbuild.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.removeLastChar
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.MathUtils

class RenderTotalBuildReportStage(
    private val report: Report
): Stage<String, String> {

    companion object {
        private const val CHART_EMPTY_POSITION_RATE = 30
    }

    override suspend fun process(input: String): String {
        if (report.totalBuildReport.isNull())
            return input.replace("%totalbuild-metric%",
                "<p>Total Build Median Chart is not available!</p><div class=\"space\"></div>")

        val values = ensureNotNull(report.totalBuildReport).values.map { it.value }.toIntList()

        val labels = StringBuilder()
        ensureNotNull(report.totalBuildReport).values.map { it.description }.whenEach {
            labels.append("\"$this\"").append(",")
        }
        // because the last item should not have ',' separator.
        labels.removeLastChar()

        val chartMaxValue = MathUtils.sumWithPercentage(
            ensureNotNull(report.totalBuildReport).maxValue,
            CHART_EMPTY_POSITION_RATE
        )
        val chartMinValue = MathUtils.deductWithPercentage(
            ensureNotNull(report.totalBuildReport).minValue,
            CHART_EMPTY_POSITION_RATE
        )

        var template = getTextResourceContent("totalbuild-metric-template.html")

        template = template
            .replace("%totalbuild-max-value%", chartMaxValue.toString())
            .replace("%totalbuild-min-value%", chartMinValue.toString())
            .replace("%totalbuild-median-values%", values.toString())
            .replace("%totalbuild-median-labels%", labels.toString())

        return input.replace("%totalbuild-metric%", template)
    }

}
