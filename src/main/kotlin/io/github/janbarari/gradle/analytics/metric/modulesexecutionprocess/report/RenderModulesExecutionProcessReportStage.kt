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
package io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.toArrayString
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.HtmlUtils
import io.github.janbarari.gradle.utils.MathUtils

/**
 * Generates html result for [io.github.janbarari.gradle.analytics.domain.model.report.ModulesExecutionProcessReport].
 */
class RenderModulesExecutionProcessReportStage(
    private val report: Report
) : Stage<String, String> {

    companion object {
        private const val CHART_SUGGESTED_MIN_MAX_PERCENTAGE = 30
        private const val MODULES_EXECUTION_PROCESS_METRIC_TEMPLATE_ID = "%modules-execution-process-metric%"
        private const val MODULES_EXECUTION_PROCESS_METRIC_FILE_NAME = "modules-execution-process-metric-template"
    }

    override suspend fun process(input: String): String {
        if (report.modulesExecutionProcessReport.isNull()) {
            return input.replace(MODULES_EXECUTION_PROCESS_METRIC_TEMPLATE_ID, getEmptyRender())
        }

        return input.replace(MODULES_EXECUTION_PROCESS_METRIC_TEMPLATE_ID, getMetricRender())
    }

    @Suppress("LongMethod")
    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(MODULES_EXECUTION_PROCESS_METRIC_FILE_NAME)
        report.modulesExecutionProcessReport.whenNotNull {

            val min = ensureNotNull(report.modulesExecutionProcessReport).modules.minOfOrNull { it.avgMedianDuration } ?: 0L
            val max = ensureNotNull(report.modulesExecutionProcessReport).modules.maxOfOrNull { it.avgMedianDuration } ?: 0L

            val chartSuggestedMinValue = MathUtils.deductWithPercentage(
                min / 1000L,
                CHART_SUGGESTED_MIN_MAX_PERCENTAGE
            )
            val chartSuggestedMaxValue = MathUtils.sumWithPercentage(
                max / 1000L,
                CHART_SUGGESTED_MIN_MAX_PERCENTAGE
            )

            val chartLabels: String = modules
                .firstOrNull()
                ?.avgMedianDurations
                ?.map { it.description }
                ?.toArrayString()
                ?: "[]"

            val chartDatasets = buildString {
                modules.whenEach {
                    append("{")
                    append("label: \"$path\",")
                    append("fill: false,")
                    append("borderColor: getColor(),")
                    append("backgroundColor: shadeColor(getColor(), 25),")
                    append("pointRadius: 0,")
                    append("data: ${avgMedianDurations.map { it.value / 1000L }.toIntList()},")
                    append("cubicInterpolationMode: 'monotone',")
                    append("tension: 0.4,")
                    append("hidden: false")
                    append("}")
                    append(",")
                }
            }

            val tableData = buildString {
                modules.forEachIndexed { i, module ->
                    append("<tr>")
                    append("<th>${i+1}</th>")
                    append("<th>${module.path}</th>")
                    append("<th>${module.avgMedianDuration / 1000L}s</th>")
                    append("<th>${module.avgMedianParallelDuration / 1000L}s</th>")
                    append("<th>${module.avgMedianParallelRate}%</th>")
                    append("<th>${module.avgMedianCoverage}%</th>")
                    if (module.diffRate.isNull()) {
                        append("<th>Unknown</th>")
                    } else if(module.diffRate!! > 0) {
                        append("<th class=\"red\">${module.diffRate}%</th>")
                    } else {
                        append("<th class=\"green\">${module.diffRate}%</th>")
                    }
                    append("</tr>")
                }
            }

            renderedTemplate = renderedTemplate
                .replace("%suggested-min-value%", chartSuggestedMinValue.toString())
                .replace("%suggested-max-value%", chartSuggestedMaxValue.toString())
                .replace("%chart-labels%", chartLabels)
                .replace("%chart-datasets%", chartDatasets)
                .replace("%table-data%", tableData)
        }
        return renderedTemplate
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Modules Execution Process is not available!")
    }

}
