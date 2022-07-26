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
package io.github.janbarari.gradle.analytics.metric.configuration.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.removeLastChar
import io.github.janbarari.gradle.extension.toArrayString
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.HtmlUtils
import io.github.janbarari.gradle.utils.MathUtils

/**
 * Generates html result for [io.github.janbarari.gradle.analytics.domain.model.report.ConfigurationReport].
 */
class RenderConfigurationReportStage(
    private val report: Report
) : Stage<String, String> {

    companion object {
        private const val CHART_SUGGESTED_MIN_MAX_PERCENTAGE = 30
        private const val CONFIGURATION_METRIC_TEMPLATE_ID = "%configuration-metric%"
        private const val CONFIGURATION_METRIC_TEMPLATE_FILE_NAME = "configuration-metric-template"
    }

    override suspend fun process(input: String): String {
        if (report.configurationReport.isNull()) {
            return input.replace(CONFIGURATION_METRIC_TEMPLATE_ID, getEmptyRender())
        }

        return input.replace(CONFIGURATION_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(CONFIGURATION_METRIC_TEMPLATE_FILE_NAME)
        report.configurationReport.whenNotNull {
            val chartValues = values.map { it.value }
                .toIntList()
                .toString()

            val chartLabels = values.map { it.description }
                .toArrayString()

            val chartSuggestedMaxValue = MathUtils.sumWithPercentage(maxValue, CHART_SUGGESTED_MIN_MAX_PERCENTAGE)
            val chartSuggestedMinValue = MathUtils.deductWithPercentage(minValue, CHART_SUGGESTED_MIN_MAX_PERCENTAGE)

            renderedTemplate = renderedTemplate
                .replace("%configuration-max-value%", chartSuggestedMaxValue.toString())
                .replace("%configuration-min-value%", chartSuggestedMinValue.toString())
                .replace("%configuration-median-values%", chartValues)
                .replace("%configuration-median-labels%", chartLabels)
        }
        return renderedTemplate
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Configuration chart is not available!")
    }

}
