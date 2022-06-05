package io.github.janbarari.gradle.analytics.metric.initialization

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ChartPoint
import io.github.janbarari.gradle.analytics.domain.model.InitializationReport
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.core.Triple
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.utils.DateTimeUtils
import io.github.janbarari.gradle.utils.MathUtils

class CreateInitializationReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    companion object {
        private const val SKIP_METRIC_THRESHOLD = 50L
        private const val CHART_MAX_COLUMNS = 12
    }

    override fun process(input: Report): Report {

        val initializationChartPoints = metrics.filter { it.initializationMetric.isNotNull() }
            .filter { ensureNotNull(it.initializationMetric).average.isNotNull() }
            .filter { ensureNotNull(it.initializationMetric).average.isBiggerEquals(SKIP_METRIC_THRESHOLD) }
            .map {
                InitializationChartPoint(
                    value = ensureNotNull(it.initializationMetric).average,
                    startedAt = it.createdAt,
                    finishedAt = null
                )
            }

        val initializationMetricsMean = resizeInitializationChartPoints(initializationChartPoints, CHART_MAX_COLUMNS)

        if (initializationMetricsMean.isEmpty()) return input

        val initializationReport = InitializationReport(
            values = initializationMetricsMean.map {
                val period = if (it.finishedAt.isNull()) {
                    DateTimeUtils.format(it.startedAt, "dd/MM")
                } else {
                    DateTimeUtils.format(it.startedAt, "dd/MM") + "-" +
                            DateTimeUtils.format(ensureNotNull(it.finishedAt), "dd/MM")
                }
                ChartPoint(it.value, period)
            },
            maxValue = initializationMetricsMean.maxOf { it.first },
            minValue = initializationMetricsMean.minOf { it.first }
        )

        input.initializationReport = initializationReport

        return input
    }

    fun resizeInitializationChartPoints(
        input: List<InitializationChartPoint>, targetSize: Int
    ): List<InitializationChartPoint> {
        return if (input.size > targetSize)
            resizeInitializationChartPoints(calculatePointsMean(input), targetSize)
        else input
    }

    fun calculatePointsMean(values: List<InitializationChartPoint>): List<InitializationChartPoint> {

        if (values.isEmpty()) return values

        val mean = arrayListOf<InitializationChartPoint>()
        val size = values.size
        var nextIndex = 0

        for (i in values.indices) {
            if (i < nextIndex) continue

            if (i + 1 >= size) {
                mean.add(values[i])
            } else {

                var finishedAt = values[i + 1].finishedAt
                if (finishedAt.isNull()) finishedAt = values[i + 1].startedAt

                mean.add(
                    InitializationChartPoint(
                        value = MathUtils.longMean(values[i].value, values[i + 1].value),
                        startedAt = values[i].startedAt,
                        finishedAt = finishedAt
                    )
                )

                nextIndex = i + 2
            }
        }

        return mean
    }

    class InitializationChartPoint(
        val value: Long,
        val startedAt: Long,
        val finishedAt: Long? = null
    ) : Triple<Long, Long, Long?>(value, startedAt, finishedAt)

}
