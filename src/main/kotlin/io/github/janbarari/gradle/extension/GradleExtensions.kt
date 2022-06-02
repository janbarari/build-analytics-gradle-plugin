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
package io.github.janbarari.gradle.extension

import io.github.janbarari.gradle.ExcludeJacocoGenerated
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.Provider

/**
 * Returns the Gradle requested tasks list. `requestedTasks` are the tasks that CLI
 * sent them to Gradle to start the build process.
 */
@ExcludeJacocoGenerated
fun Gradle.getRequestedTasks(): List<String> {
    return startParameter.taskNames
}

@ExcludeJacocoGenerated
fun Project.envCI(): Provider<String> {
    return providers.environmentVariable("CI")
}

@ExcludeJacocoGenerated
inline fun <reified T: DefaultTask> Project.registerTask(name: String, crossinline block: T.() -> Unit) {
    project.tasks.register(name, T::class.java) {
        it.also(block)
    }
}
