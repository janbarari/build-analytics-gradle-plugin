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
package io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.report

import io.github.janbarari.gradle.analytics.domain.model.report.ModuleBuildHeatmap
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesBuildHeatmapReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RenderModulesBuildHeatmapReportStageTest {

    @Test
    fun `check render when report is null`() = runBlocking {
        val report = Report("main", "assemble")

        val renderTemplate = "%modules-build-heatmap-metric%"
        val stage = RenderModulesBuildHeatmapReportStage(report)
        val result = stage.process(renderTemplate)

        val expectedAnswer = "<p>Modules Build Heatmap is not available!</p><div class=\"space\"></div>"
        assertEquals(expectedAnswer, result)
    }

    @Test
    fun `check render when modules list is empty`() = runBlocking {
        val report = Report("main", "assemble")
        report.modulesBuildHeatmapReport = ModulesBuildHeatmapReport(
            emptyList()
        )

        val renderTemplate = "%modules-build-heatmap-metric%"
        val stage = RenderModulesBuildHeatmapReportStage(report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("labels: []")
        }

        assertTrue {
            result.contains("data: []")
        }
    }

    @Test
    fun `check render when report is available`() = runBlocking {
        val report = Report("main", "assemble")
        report.modulesBuildHeatmapReport = ModulesBuildHeatmapReport(
            listOf(
                ModuleBuildHeatmap(":woman", 2, 60, 2),
                ModuleBuildHeatmap(":life", 4, 40, 2),
                ModuleBuildHeatmap(":freedom", 6, 20, 2)
            )
        )

        val renderTemplate = "%modules-build-heatmap-metric%"
        val stage = RenderModulesBuildHeatmapReportStage(report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("labels: [\":freedom | 6D\",\":life | 4D\",\":woman | 2D\"]")
        }

        assertTrue {
            result.contains("data: [2, 2, 1]")
        }
    }

}
