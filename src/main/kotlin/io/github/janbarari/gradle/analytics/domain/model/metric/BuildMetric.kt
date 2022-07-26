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
package io.github.janbarari.gradle.analytics.domain.model.metric

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
@JsonClass(generateAdapter = true)
data class BuildMetric(

    @Json(name = "branch")
    var branch: String,

    @Json(name = "requestedTasks")
    var requestedTasks: List<String>,

    @Json(name = "created_at")
    var createdAt: Long,

    @Json(name = "initialization_metric")
    var initializationMetric: InitializationMetric? = null,

    @Json(name = "configuration_metric")
    var configurationMetric: ConfigurationMetric? = null,

    @Json(name = "execution_metric")
    var executionMetric: ExecutionMetric? = null,

    @Json(name = "total_build_metric")
    var totalBuildMetric: TotalBuildMetric? = null,

    @Json(name = "modules_source_count_metric")
    var modulesSourceCountMetric: ModulesSourceCountMetric? = null,

    @Json(name = "modules_method_count_metric")
    var modulesMethodCountMetric: ModulesMethodCountMetric? = null,

    @Json(name = "cache_hit_metric")
    var cacheHitMetric: CacheHitMetric? = null

): java.io.Serializable
