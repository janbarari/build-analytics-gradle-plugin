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
package io.github.janbarari.gradle.analytics.scanner.execution

import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig.DatabaseConfig
import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.os.HardwareInfo
import io.github.janbarari.gradle.analytics.domain.model.os.OsInfo
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.UpsertModulesTimelineUseCase
import io.github.janbarari.gradle.analytics.metric.cachehit.create.CreateCacheHitMetricStage
import io.github.janbarari.gradle.analytics.metric.cachehit.create.CreateCacheHitMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configurationprocess.create.CreateConfigurationProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.configurationprocess.create.CreateConfigurationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencydetails.create.CreateDependencyDetailsMetricStage
import io.github.janbarari.gradle.analytics.metric.dependencydetails.create.CreateDependencyDetailsMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.create.CreateDependencyResolveProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.create.CreateDependencyResolveProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.executionprocess.create.CreateExecutionProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.executionprocess.create.CreateExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initializationprocess.create.CreateInitializationProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.initializationprocess.create.CreateInitializationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.create.CreateModulesBuildHeatmapMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.create.CreateModulesBuildHeatmapMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulescrashcount.create.CreateModulesCrashCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulescrashcount.create.CreateModulesCrashCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesdependencygraph.create.CreateModulesDependencyGraphMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesdependencygraph.create.CreateModulesDependencyGraphMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.create.CreateModulesExecutionProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.create.CreateModulesExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create.CreateModulesMethodCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create.CreateModulesMethodCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.create.CreateModulesSourceCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.create.CreateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulessourcesize.create.CreateModulesSourceSizeMetricStage
import io.github.janbarari.gradle.analytics.metric.modulessourcesize.create.CreateModulesSourceSizeMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulestimeline.create.CreateModulesTimelineMetricStage
import io.github.janbarari.gradle.analytics.metric.modulestimeline.create.CreateModulesTimelineMetricUseCase
import io.github.janbarari.gradle.analytics.metric.noncacheabletasks.create.CreateNonCacheableTasksMetricStage
import io.github.janbarari.gradle.analytics.metric.noncacheabletasks.create.CreateNonCacheableTasksMetricUseCase
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.create.CreateOverallBuildProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.create.CreateOverallBuildProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.create.CreateParallelExecutionRateMetricStage
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.create.CreateParallelExecutionRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.successbuildrate.create.CreateSuccessBuildRateMetricStage
import io.github.janbarari.gradle.analytics.metric.successbuildrate.create.CreateSuccessBuildRateMetricUseCase
import io.github.janbarari.gradle.analytics.reporttask.ReportAnalyticsTask
import io.github.janbarari.gradle.analytics.scanner.configuration.BuildConfigurationService
import io.github.janbarari.gradle.analytics.scanner.dependencyresolution.BuildDependencyResolutionService
import io.github.janbarari.gradle.analytics.scanner.initialization.BuildInitializationService
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace
import io.github.janbarari.gradle.os.provideHardwareInfo
import io.github.janbarari.gradle.os.provideOperatingSystem
import io.github.janbarari.gradle.utils.ConsolePrinter
import io.github.janbarari.gradle.utils.DateTimeUtils
import io.github.janbarari.gradle.utils.GitUtils
import kotlinx.coroutines.runBlocking

/**
 * Implementation of [io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionLogic].
 */
class BuildExecutionLogicImp(
    private val saveMetricUseCase: SaveMetricUseCase,
    private val saveTemporaryMetricUseCase: SaveTemporaryMetricUseCase,
    private val upsertModulesTimelineUseCase: UpsertModulesTimelineUseCase,
    private val createInitializationProcessMetricUseCase: CreateInitializationProcessMetricUseCase,
    private val createConfigurationProcessMetricUseCase: CreateConfigurationProcessMetricUseCase,
    private val createExecutionProcessMetricUseCase: CreateExecutionProcessMetricUseCase,
    private val createOverallBuildProcessMetricUseCase: CreateOverallBuildProcessMetricUseCase,
    private val createModulesSourceCountMetricUseCase: CreateModulesSourceCountMetricUseCase,
    private val createModulesMethodCountMetricUseCase: CreateModulesMethodCountMetricUseCase,
    private val createCacheHitMetricUseCase: CreateCacheHitMetricUseCase,
    private val createSuccessBuildRateMetricUseCase: CreateSuccessBuildRateMetricUseCase,
    private val createDependencyResolveProcessMetricUseCase: CreateDependencyResolveProcessMetricUseCase,
    private val createParallelExecutionRateMetricUseCase: CreateParallelExecutionRateMetricUseCase,
    private val createModulesExecutionProcessMetricUseCase: CreateModulesExecutionProcessMetricUseCase,
    private val createModulesDependencyGraphMetricUseCase: CreateModulesDependencyGraphMetricUseCase,
    private val createModulesTimelineMetricUseCase: CreateModulesTimelineMetricUseCase,
    private val createModulesBuildHeatmapMetricUseCase: CreateModulesBuildHeatmapMetricUseCase,
    private val createDependencyDetailsMetricUseCase: CreateDependencyDetailsMetricUseCase,
    private val createNonCacheableTasksMetricUseCase: CreateNonCacheableTasksMetricUseCase,
    private val createModulesSourceSizeMetricUseCase: CreateModulesSourceSizeMetricUseCase,
    private val createModulesCrashCountMetricUseCase: CreateModulesCrashCountMetricUseCase,
    private val databaseConfig: DatabaseConfig,
    private val envCI: Boolean,
    private val trackingBranches: List<String>,
    private val trackingTasks: List<String>,
    private val requestedTasks: List<String>
) : BuildExecutionLogic {

    @Suppress("LongMethod")
    override fun onExecutionFinished(executedTasks: Collection<TaskInfo>) = runBlocking {

        if (isForbiddenTasksRequested()) return@runBlocking

        if (!isDatabaseConfigurationValid()) return@runBlocking

        if (!isTaskTrackable()) return@runBlocking

        if (!isBranchTrackable()) return@runBlocking

        val isSuccessful = executedTasks.all { it.isSuccessful }
        val failure = executedTasks.find { !it.isSuccessful && it.failures.isNotNull() }?.failures

        val buildInfo = BuildInfo(
            createdAt = System.currentTimeMillis(),
            startedAt = BuildInitializationService.STARTED_AT,
            initializedAt = BuildInitializationService.INITIALIZED_AT,
            configuredAt = BuildConfigurationService.CONFIGURED_AT,
            finishedAt = System.currentTimeMillis(),
            osInfo = OsInfo(provideOperatingSystem().getName()),
            hardwareInfo = HardwareInfo(provideHardwareInfo().availableMemory(), provideHardwareInfo().totalMemory()),
            dependenciesResolveInfo = BuildDependencyResolutionService.dependenciesResolveInfo.values,
            executedTasks = executedTasks.toList(),
            branch = GitUtils.currentBranch(),
            gitHeadCommitHash = GitUtils.getHeadCommitHash(),
            requestedTasks = requestedTasks,
            isSuccessful = isSuccessful,
            failure = failure
        )

        resetDependentServices()

        val createInitializationProcessMetricStage =
            CreateInitializationProcessMetricStage(buildInfo, createInitializationProcessMetricUseCase)
        val createConfigurationProcessMetricStage =
            CreateConfigurationProcessMetricStage(buildInfo, createConfigurationProcessMetricUseCase)
        val createExecutionProcessMetricStage =
            CreateExecutionProcessMetricStage(buildInfo, createExecutionProcessMetricUseCase)
        val createOverallBuildProcessMetricStage =
            CreateOverallBuildProcessMetricStage(buildInfo, createOverallBuildProcessMetricUseCase)
        val createModulesSourceCountMetricStage =
            CreateModulesSourceCountMetricStage(createModulesSourceCountMetricUseCase)
        val createModulesMethodCountMetricStage =
            CreateModulesMethodCountMetricStage(createModulesMethodCountMetricUseCase)
        val createCacheHitMetricStage = CreateCacheHitMetricStage(buildInfo, createCacheHitMetricUseCase)
        val createSuccessBuildRateMetricStage =
            CreateSuccessBuildRateMetricStage(buildInfo, createSuccessBuildRateMetricUseCase)
        val createDependencyResolveProcessMetricStage =
            CreateDependencyResolveProcessMetricStage(buildInfo, createDependencyResolveProcessMetricUseCase)
        val createParallelExecutionRateMetricStage =
            CreateParallelExecutionRateMetricStage(buildInfo, createParallelExecutionRateMetricUseCase)
        val createModulesExecutionProcessMetricStage = CreateModulesExecutionProcessMetricStage(
            buildInfo, createModulesExecutionProcessMetricUseCase
        )
        val createModulesDependencyGraphMetricStage = CreateModulesDependencyGraphMetricStage(
            createModulesDependencyGraphMetricUseCase
        )
        val createModulesTimelineMetricStage = CreateModulesTimelineMetricStage(
            buildInfo,
            createModulesTimelineMetricUseCase
        )
        val createModulesBuildHeatmapMetricStage = CreateModulesBuildHeatmapMetricStage(
            createModulesBuildHeatmapMetricUseCase
        )
        val createDependencyDetailsMetricStage = CreateDependencyDetailsMetricStage(
            createDependencyDetailsMetricUseCase
        )
        val createNonCacheableTasksMetricStage = CreateNonCacheableTasksMetricStage(
            buildInfo,
            createNonCacheableTasksMetricUseCase
        )
        val createModulesSourceSizeMetricStage = CreateModulesSourceSizeMetricStage(
            createModulesSourceSizeMetricUseCase
        )
        val createModulesCrashCountMetricStage = CreateModulesCrashCountMetricStage(
            buildInfo,
            createModulesCrashCountMetricUseCase
        )

        val buildMetric = CreateMetricPipeline(createInitializationProcessMetricStage)
            .addStage(createConfigurationProcessMetricStage)
            .addStage(createExecutionProcessMetricStage)
            .addStage(createOverallBuildProcessMetricStage)
            .addStage(createModulesSourceCountMetricStage)
            .addStage(createModulesMethodCountMetricStage)
            .addStage(createCacheHitMetricStage)
            .addStage(createSuccessBuildRateMetricStage)
            .addStage(createDependencyResolveProcessMetricStage)
            .addStage(createParallelExecutionRateMetricStage)
            .addStage(createModulesExecutionProcessMetricStage)
            .addStage(createModulesDependencyGraphMetricStage)
            .addStage(createModulesTimelineMetricStage)
            .addStage(createModulesBuildHeatmapMetricStage)
            .addStage(createDependencyDetailsMetricStage)
            .addStage(createNonCacheableTasksMetricStage)
            .addStage(createModulesSourceSizeMetricStage)
            .addStage(createModulesCrashCountMetricStage)
            .execute(BuildMetric(buildInfo.branch, buildInfo.requestedTasks, buildInfo.createdAt, buildInfo.gitHeadCommitHash))

        saveTemporaryMetricUseCase.execute(buildMetric)
        saveMetricUseCase.execute(buildMetric)

        if (buildMetric.modulesTimelineMetric.isNotNull()) {
            upsertModulesTimelineUseCase.execute(buildInfo.branch to buildMetric.modulesTimelineMetric!!)
        }

        printBuildInfo(buildMetric)
    }

    private fun printBuildInfo(buildMetric: BuildMetric) {
        val requestedTasks = buildMetric.requestedTasks.separateElementsWithSpace()
        val repoLink = "https://github.com/janbarari/gradle-analytics-plugin"

        var width = requestedTasks.length + 25
        if (width < 60) width = 60

        ConsolePrinter(width).run {
            printFirstLine()
            printLine(left = "Gradle Analytics Plugin")
            printBreakLine(char = '-')
            printLine(left = "Requested Tasks:", right = requestedTasks)
            printLine(left = "Branch:", right = buildMetric.branch)
            printLine(left = "Head Commit Hash:", right = buildMetric.gitHeadCommitHash)
            printBreakLine(char = '-')
            printLine(left = "Initialization Process:", right = "${buildMetric.initializationProcessMetric?.median}ms")
            printLine(left = "Configuration Process:", right = "${buildMetric.configurationProcessMetric?.median}ms")
            printLine(left = "Dependency Resolve Process:", right = "${buildMetric.dependencyResolveProcessMetric?.median}ms")
            printLine(left = "Execution Process:", right = "${buildMetric.executionProcessMetric?.median}ms")
            printLine(left = "Overall Build Process:", right = "${buildMetric.overallBuildProcessMetric?.median}ms")
            printLine(left = "Cache Hit:", right = "${buildMetric.cacheHitMetric?.rate}%")
            printLine(left = "Parallel Execution Rate:", right = "${buildMetric.parallelExecutionRateMetric?.medianRate}%")
            printBreakLine(char = '-')
            printLine(left = "Datetime:", right = DateTimeUtils.formatToDateTime(buildMetric.createdAt))
            printBreakLine(char = '-')
            printLine(left = "Made with ❤ for everyone")
            printLine(left = repoLink)
            printLine(right = "↖ Tap the ☆ button to support us")
            printLastLine()
        }

    }

    @ExcludeJacocoGenerated
    override fun resetDependentServices() {
        BuildInitializationService.reset()
        BuildConfigurationService.reset()
        BuildDependencyResolutionService.reset()
    }

    override fun isDatabaseConfigurationValid(): Boolean {
        //return false if local machine executed and the config is not set.
        if (databaseConfig.local.isNull() && !envCI) {
            return false
        }

        //return false if CI machine executed and the config is not set.
        if (databaseConfig.ci.isNull() && envCI) {
            return false
        }

        return true
    }

    override fun isForbiddenTasksRequested(): Boolean {
        return requestedTasks.contains(ReportAnalyticsTask.TASK_NAME)
    }

    override fun isTaskTrackable(): Boolean {
        val requestedTasks = requestedTasks.separateElementsWithSpace()
        return trackingTasks.contains(requestedTasks)
    }

    override fun isBranchTrackable(): Boolean {
        return trackingBranches.contains(GitUtils.currentBranch())
    }

}
