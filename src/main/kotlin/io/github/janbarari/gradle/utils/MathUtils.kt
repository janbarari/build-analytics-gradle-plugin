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
package io.github.janbarari.gradle.utils

/**
 * A collection of mathematics functions.
 */
object MathUtils {

    /**
     * Calculate the Mean average of a long dataset.
     */
    fun longMean(vararg dataset: Long): Long {
        return longMean(dataset.toList())
    }

    /**
     * Calculate the Mean average of a long dataset.
     */
    fun longMean(dataset: List<Long>): Long {
        if (dataset.isEmpty()) return 0
        return dataset.sum() / dataset.size
    }

    /**
     * Calculate the Median average of a long dataset.
     */
    fun longMedian(dataset: List<Long>): Long {
        if (dataset.isEmpty()) return 0
        return dataset.sorted().let {
            if (it.size % 2 == 0) (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
            else it[it.size / 2]
        }
    }

    /**
     * Calculates the Median average of a float dataset.
     */
    fun floatMedian(dataset: List<Float>): Float {
        if (dataset.isEmpty()) return 0F
        return dataset.sorted().let {
            if (it.size % 2 == 0) ((it[it.size / 2] + it[(it.size - 1) / 2]) / 2)
            else it[it.size / 2]
        }
    }

    /**
     * Calculate the Median average of a long dataset.
     */
    fun longMedian(vararg values: Long): Long {
        return longMedian(values.toList())
    }

    /**
     * Add to the value by defined percentage.
     */
    fun sumWithPercentage(value: Long, percentage: Int): Long {
        return value + ((value * percentage) / 100L)
    }

    /**
     * Deduct the value by defined percentage.
     */
    fun deductWithPercentage(value: Long, percentage: Int): Long {
        return value - ((value * percentage) / 100L)
    }

}
