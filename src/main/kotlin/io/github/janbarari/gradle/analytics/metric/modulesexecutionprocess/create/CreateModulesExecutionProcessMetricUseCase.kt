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
package io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.create

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleExecutionProcess
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesExecutionProcessMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.toPercentageOf
import io.github.janbarari.gradle.extension.whenEach

class CreateModulesExecutionProcessMetricUseCase: UseCase<Pair<List<ModulePath>, BuildInfo>, ModulesExecutionProcessMetric>() {

    override suspend fun execute(input: Pair<List<ModulePath>, BuildInfo>): ModulesExecutionProcessMetric {
        val modulePaths = input.first
        val buildInfo = input.second

        val moduleExecutionProcesses = mutableListOf<ModuleExecutionProcess>()

        modulePaths.whenEach {
            val tasks = buildInfo.executedTasks.filter { it.path.startsWith(path) }

            val overallDuration = buildInfo.getExecutionDuration().toMillis()
            val moduleParallelDuration = tasks.sumOf { it.getDuration() }
            val moduleNonParallelDuration = calculateNonParallelExecutionDuration(tasks)
            val moduleParallelRate = (moduleParallelDuration - moduleNonParallelDuration)
                .toPercentageOf(moduleNonParallelDuration)
            val moduleCoverage = moduleNonParallelDuration.toPercentageOf(overallDuration)

            moduleExecutionProcesses.add(
                ModuleExecutionProcess(
                    path = path,
                    duration = moduleNonParallelDuration,
                    parallelDuration = moduleParallelDuration,
                    parallelRate = moduleParallelRate,
                    coverage = moduleCoverage
                )
            )
        }

        return ModulesExecutionProcessMetric(
            modules = moduleExecutionProcesses
        )
    }

    @Suppress("NestedBlockDepth", "ComplexMethod")
    private fun calculateNonParallelExecutionDuration(executedTasks: Collection<TaskInfo>): Long {

        fun checkIfCanMerge(
            parallelTaskReport: TaskInfo,
            linearTask: Map.Entry<Int, Pair<Long, Long>>,
            linearTasks: HashMap<Int, Pair<Long, Long>>
        ) {

            if (parallelTaskReport.startedAt <= linearTask.value.second &&
                parallelTaskReport.finishedAt >= linearTask.value.second
            ) {
                var start = linearTask.value.first
                var end = linearTask.value.second
                if (parallelTaskReport.startedAt < linearTask.value.first) {
                    start = parallelTaskReport.startedAt
                }
                if (parallelTaskReport.finishedAt > linearTask.value.second) {
                    end = parallelTaskReport.finishedAt
                }
                linearTasks[linearTask.key] = Pair(start, end)
            }

        }

        val nonParallelDurations = hashMapOf<Int, Pair<Long, Long>>()

        val iterator = executedTasks
            .sortedBy { task -> task.startedAt }
            .iterator()

        while (iterator.hasNext()) {

            val executedTask = iterator.next()
            if (nonParallelDurations.isEmpty()) {
                nonParallelDurations[nonParallelDurations.size] = Pair(executedTask.startedAt, executedTask.finishedAt)
                continue
            }

            var shouldBeAddedItem: Pair<Long, Long>? = null
            val linearTasksIterator = nonParallelDurations.iterator()
            while (linearTasksIterator.hasNext()) {
                val linearTask = linearTasksIterator.next()

                checkIfCanMerge(executedTask, linearTask, nonParallelDurations)

                if (executedTask.startedAt > linearTask.value.first &&
                    executedTask.finishedAt > linearTask.value.second &&
                    executedTask.finishedAt > executedTask.startedAt
                ) {
                    shouldBeAddedItem = Pair(executedTask.startedAt, executedTask.finishedAt)
                }

            }

            shouldBeAddedItem?.let { new ->
                val itr = nonParallelDurations.iterator()
                while (itr.hasNext()) {
                    val linearTask = itr.next()

                    if (new.first <= linearTask.value.second &&
                        new.second >= linearTask.value.second
                    ) {
                        var start = linearTask.value.first
                        var end = linearTask.value.second
                        if (new.first < linearTask.value.first) {
                            start = new.first
                        }
                        if (new.second > linearTask.value.second) {
                            end = new.second
                        }
                        nonParallelDurations[linearTask.key] = Pair(start, end)
                    }
                }

                val biggestLTEnd = nonParallelDurations.toList().maxByOrNull { it.second.second }!!.second.second
                if (new.first > biggestLTEnd) {
                    nonParallelDurations[nonParallelDurations.size] = new
                }

            }

        }

        return nonParallelDurations.toList().sumOf { (it.second.second - it.second.first) }
    }

}
