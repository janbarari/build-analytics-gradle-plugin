/**
 * MIT License
 * Copyright (c) 2024 Mehdi Janbarari (@janbarari)
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
package io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.create

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.metric.ParallelExecutionRateMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.toPercentageOf
import io.github.janbarari.gradle.logger.Tower

class CreateParallelExecutionRateMetricUseCase(
    private val tower: Tower
) : UseCase<BuildInfo, ParallelExecutionRateMetric>() {

    companion object {
        private val clazz = CreateParallelExecutionRateMetricUseCase::class.java
    }

    override suspend fun execute(input: BuildInfo): ParallelExecutionRateMetric {
        tower.i(clazz, "execute()")
        val nonParallelExecutionInMillis = input.calculateNonParallelExecutionInMillis()
        val parallelExecutionInMillis = input.calculateParallelExecutionByMillis()
        val rate = (parallelExecutionInMillis - nonParallelExecutionInMillis)
            .toPercentageOf(nonParallelExecutionInMillis)
            .toLong()

        return ParallelExecutionRateMetric(medianRate = rate)
    }

}
