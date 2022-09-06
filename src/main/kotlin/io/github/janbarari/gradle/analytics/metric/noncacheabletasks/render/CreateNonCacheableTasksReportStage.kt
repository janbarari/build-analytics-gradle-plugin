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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.metric.noncacheabletasks.render

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.NonCacheableTasksReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isBigger
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.modify
import io.github.janbarari.gradle.utils.MathUtils

class CreateNonCacheableTasksReportStage(
    private val metrics: List<BuildMetric>
): Stage<Report, Report> {

    override suspend fun process(report: Report): Report {
        return report.apply {
            val tasks = metrics.last().nonCacheableTasksMetric?.tasks
                ?.modify {
                    val medianValue = metrics
                        .filter { it.nonCacheableTasksMetric.isNotNull() }
                        .flatMap { metric ->
                            metric.nonCacheableTasksMetric!!.tasks
                                .filter { it.path == path }
                                .map { it.avgExecutionDurationInMillis }
                        }
                    avgExecutionDurationInMillis = MathUtils.longMedian(medianValue)
                }
                ?.filter { it.avgExecutionDurationInMillis.isBigger(0) }
                ?: emptyList()

            nonCacheableTasksReport = NonCacheableTasksReport(
                tasks = tasks
            )
        }
    }

}
