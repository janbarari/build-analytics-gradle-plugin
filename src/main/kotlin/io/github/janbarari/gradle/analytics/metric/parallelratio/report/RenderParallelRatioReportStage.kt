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
package io.github.janbarari.gradle.analytics.metric.parallelratio.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.toArrayString
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.HtmlUtils
import io.github.janbarari.gradle.utils.MathUtils

/**
 * Generates html result for [io.github.janbarari.gradle.analytics.domain.model.report.ParallelRatioReport]
 */
class RenderParallelRatioReportStage(
    private val report: Report
): Stage<String, String> {

    companion object {
        private const val CHART_SUGGESTED_MAX_PERCENTAGE = 30
        private const val PARALLEL_RATIO_METRIC_TEMPLATE_ID = "%parallel-ratio-metric%"
        private const val PARALLEL_RATIO_METRIC_TEMPLATE_FILE_NAME = "parallel-ratio-metric-template"
    }

    override suspend fun process(input: String): String {
        if (report.parallelRatioReport.isNull())
            return input.replace(PARALLEL_RATIO_METRIC_TEMPLATE_ID, getEmptyRender())

        return input.replace(PARALLEL_RATIO_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(PARALLEL_RATIO_METRIC_TEMPLATE_FILE_NAME)
        report.parallelRatioReport.whenNotNull {
            val chartValues = values.map { it.value }
                .toIntList()
                .toString()

            val chartLabels = values.map { it.description }
                .toArrayString()

            val chartSuggestedMaxValue = MathUtils.sumWithPercentage(maxValue, CHART_SUGGESTED_MAX_PERCENTAGE)
            val chartSuggestedMinValue = 0

            renderedTemplate = renderedTemplate
                .replace("%chart-values%", chartValues)
                .replace("%chart-labels%", chartLabels)
                .replace("%suggested-min-value%", chartSuggestedMinValue.toString())
                .replace("%suggested-max-value%", chartSuggestedMaxValue.toString())
        }
        return renderedTemplate
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Parallel ratio is not available!")
    }

}
