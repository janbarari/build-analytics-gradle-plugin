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
package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.analytics.reporttask.exception.InvalidPropertyException
import io.github.janbarari.gradle.analytics.reporttask.exception.MissingPropertyException
import java.io.IOException

interface ReportAnalyticsLogic {

    @kotlin.jvm.Throws(IOException::class)
    suspend fun saveReport(renderedHTML: String): String

    suspend fun generateReport(branch: String, requestedTasks: String, period: String): String

    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    fun ensureBranchArgumentValid(branchArgument: String)

    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    fun ensurePeriodArgumentValid(periodArgument: String)

    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    fun ensureTaskArgumentValid(requestedTasksArgument: String)

    @kotlin.jvm.Throws(InvalidPropertyException::class)
    fun convertQueryToPeriod(query: String): Pair<Long, Long>

}
