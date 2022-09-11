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
package io.github.janbarari.gradle.analytics.metric.configurationprocess.report

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ConfigurationProcessReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.mapToConfigurationMeanTimespanChartPoints
import io.github.janbarari.gradle.extension.mapToConfigurationMedianTimespanChartPoints
import io.github.janbarari.gradle.extension.whenEmpty

class CreateConfigurationProcessReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    companion object {
        private const val SKIP_THRESHOLD_IN_MS = 50L
    }

    override suspend fun process(input: Report): Report {
        val medianChartPoints = metrics.filter { metric ->
            metric.configurationProcessMetric.isNotNull() &&
                    metric.configurationProcessMetric?.median?.isBiggerEquals(SKIP_THRESHOLD_IN_MS) ?: false
        }.mapToConfigurationMedianTimespanChartPoints()
            .whenEmpty {
                return input
            }

        val meanChartPoints = metrics.filter { metric ->
            metric.configurationProcessMetric.isNotNull() &&
                    metric.configurationProcessMetric?.mean?.isBiggerEquals(SKIP_THRESHOLD_IN_MS) ?: false
        }.mapToConfigurationMeanTimespanChartPoints()
            .whenEmpty {
                return input
            }

        return input.apply {
            configurationProcessReport = ConfigurationProcessReport(
                medianValues = medianChartPoints,
                meanValues = meanChartPoints,
            )
        }
    }

}
