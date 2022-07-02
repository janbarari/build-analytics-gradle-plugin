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

import io.github.janbarari.gradle.analytics.domain.model.TaskInfo

/**
 * Logics for [io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionService].
 */
interface BuildExecutionLogic {

    /**
     * Checks the current git branch exists in the project trackable branches.
     */
    fun isBranchTrackable(): Boolean

    /**
     * Checks the 'requested tasks' is exists on the project trackable tasks.
     *
     * Note: Every Gradle process started with a task(s). It is called 'requestedTasks'.
     */
    fun isTaskTrackable(): Boolean

    /**
     * Checks the 'requested tasks' is not the plugin custom tasks.
     */
    fun isForbiddenTasksRequested(): Boolean

    /**
     * Checks the database configuration is valid.
     */
    fun isDatabaseConfigurationValid(): Boolean

    /**
     * Since the build initialization, configuration, and execution process are separated
     * and based on various situations they have their own lifecycle this method resets
     * their static variables in heap memory.
     */
    fun resetDependentServices()

    /**
     * Once the build is finished, this method should be
     * invoked by [io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionService] to start
     * storing the build metrics.
     */
    fun onExecutionFinished(executedTasks: Collection<TaskInfo>)

}
