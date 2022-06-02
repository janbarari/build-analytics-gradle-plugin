package io.github.janbarari.gradle.analytics.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.data.database.table.MetricTable
import io.github.janbarari.gradle.analytics.data.database.table.TemporaryMetricTable
import io.github.janbarari.gradle.analytics.data.database.table.TemporaryMetricTable.value
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.extension.separateElementsWithSpace
import io.github.janbarari.gradle.utils.DateTimeUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere

class DatabaseRepositoryImp(
    private val db: Database,
    private val branch: String,
    private val requestedTasks: String
) : DatabaseRepository {

    private var moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private var jsonAdapter: JsonAdapter<BuildMetric> = moshi.adapter(BuildMetric::class.java)

    override fun saveNewMetric(metric: BuildMetric): Long {
        return db.transaction {
            val queryResult = MetricTable.insert {
                it[createdAt] = metric.createdAt
                it[value] = jsonAdapter.toJson(metric)
                it[branch] = metric.branch
                it[requestedTasks] = metric.requestedTasks.separateElementsWithSpace()
            }
            return@transaction queryResult[MetricTable.number]
        }
    }

    override fun saveTemporaryMetric(metric: BuildMetric): Long {
        return db.transaction {
            val queryResult = TemporaryMetricTable.insert {
                it[createdAt] = metric.createdAt
                it[value] = jsonAdapter.toJson(metric)
                it[branch] = metric.branch
                it[requestedTasks] = metric.requestedTasks.separateElementsWithSpace()
            }
            return@transaction queryResult[TemporaryMetricTable.number]
        }
    }

    override fun getDayMetric(): Pair<BuildMetric, Long> {
        return db.transaction {
            val queryResult = MetricTable.select {
                (MetricTable.createdAt greaterEq DateTimeUtils.getDayStartMs()) and
                        (MetricTable.createdAt less DateTimeUtils.getDayEndMs()) and
                        (MetricTable.branch eq branch) and
                        (MetricTable.requestedTasks eq requestedTasks)
            }.single()
            return@transaction Pair(
                jsonAdapter.fromJson(queryResult[MetricTable.value])!!, queryResult[MetricTable.number]
            )
        }
    }

    override fun isDayMetricExists(): Boolean {
        return db.transaction {
            val queryResult = MetricTable.select {
                (MetricTable.createdAt greaterEq DateTimeUtils.getDayStartMs()) and
                        (MetricTable.createdAt less DateTimeUtils.getDayEndMs()) and
                        (MetricTable.branch eq branch) and
                        (MetricTable.requestedTasks eq requestedTasks)
            }
            return@transaction queryResult.count() > 0
        }
    }

    override fun getMetrics(period: Long): List<BuildMetric> {
        return db.transaction {
            val result = arrayListOf<BuildMetric>()
            MetricTable.select {
                MetricTable.createdAt greaterEq DateTimeUtils.calculateDayInPastMonthsMs(
                    DateTimeUtils.getDayStartMs(), period
                ) and (MetricTable.branch eq branch) and (MetricTable.requestedTasks eq requestedTasks)
            }.orderBy(MetricTable.number, SortOrder.DESC).forEach {
                result.add(
                    jsonAdapter.fromJson(it[MetricTable.value])!!
                )
            }
            return@transaction result
        }
    }

    override fun updateDayMetric(number: Long, metric: BuildMetric): Boolean {
        return db.transaction {
            val queryResult = MetricTable.update({
                MetricTable.number eq number
            }) {
                it[value] = jsonAdapter.toJson(metric)
                it[createdAt] = System.currentTimeMillis()
            }
            return@transaction queryResult == 1
        }
    }

    override fun dropOutdatedTemporaryMetrics(): Boolean {
        return db.transaction {
            TemporaryMetricTable.deleteWhere {
                TemporaryMetricTable.createdAt less DateTimeUtils.getDayStartMs()
            }
            return@transaction true
        }
    }

    override fun dropMetrics(): Boolean {
        return db.transaction {
            MetricTable.deleteAll()
            return@transaction true
        }
    }

    override fun getTemporaryMetrics(): List<BuildMetric> {
        return db.transaction {
            val metrics = arrayListOf<BuildMetric>()
            val queryResult = TemporaryMetricTable.select {
                (TemporaryMetricTable.branch eq branch) and
                        (TemporaryMetricTable.requestedTasks eq requestedTasks)
            }
            queryResult.toList().forEach {
                jsonAdapter.fromJson(it[value])?.let { metric ->
                    metrics.add(metric)
                }
            }
            return@transaction metrics
        }
    }

}
