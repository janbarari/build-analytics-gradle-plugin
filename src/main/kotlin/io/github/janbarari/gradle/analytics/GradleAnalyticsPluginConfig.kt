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
package io.github.janbarari.gradle.analytics

import groovy.lang.Closure
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import org.gradle.api.Project

/**
 * Configuration options for the [io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin].
 */
@ExcludeJacocoGenerated
open class GradleAnalyticsPluginConfig(val project: Project) {

    private var databaseConfig: DatabaseConfig = DatabaseConfig()

    var enabled: Boolean = true

    var trackingTasks: Set<String> = setOf()

    var trackingBranches: Set<String> = setOf()

    var excludeModules: Set<String> = setOf()

    /**
     * Tracing all branches has disabled by default during to the significant drawback
     * it creates in the database, especially for temporary branches.
     */
    var trackAllBranchesEnabled: Boolean = false

    var outputPath: String = project.rootProject.buildDir.absolutePath

    fun database(closure: Closure<*>) {
        closure.delegate = databaseConfig
        closure.call()
    }

    fun database(block: DatabaseConfig.() -> Unit) {
        databaseConfig = DatabaseConfig().also(block)
    }

    fun getDatabaseConfig(): DatabaseConfig = databaseConfig
}
