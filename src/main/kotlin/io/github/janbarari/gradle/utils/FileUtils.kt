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

import io.github.janbarari.gradle.extension.isSourcePath
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.io.path.name

/**
 * A collection of datetime functions.
 */
object FileUtils {

    /**
     * Checks the given directory is a module directory.
     */
    fun isModulePath(directory: String): Boolean {
        val isBuildscriptsExists = Files.list(Path(directory)).anyMatch { path ->
            path.fileName.name == "build.gradle.kts" || path.fileName.name == "build.gradle"
        }
        val isSrcDirExists = Files.list(Path(directory)).anyMatch { path ->
            path.fileName.name == "src"
        }
        return isBuildscriptsExists && isSrcDirExists
    }

    /**
     * Returns list of source files in the module path.
     */
    fun getModuleSources(directory: String): List<Path> {
        var sourcePaths: List<Path>
        Files.walk(Path(directory)).use { stream ->
            sourcePaths = stream.map { obj: Path -> obj.normalize() }
                .filter { it.isSourcePath() }
                .collect(Collectors.toList())
        }
        return sourcePaths
    }

}
