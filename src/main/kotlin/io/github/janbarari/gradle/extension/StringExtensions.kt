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

/**
 * Check the given string has any space.
 */
fun String.hasSpace(): Boolean {
    return this.contains(" ")
}

/**
 * Remove the latest character of given string.
 */
fun String.removeLast(): String {
    return substring(0, length - 1)
}

/**
 * Convert the given path string to real path.
 */
fun String.toRealPath(): String {
    if (endsWith("/")) {
        return removeLast().toRealPath()
    }
    return this
}

fun List<String>.separateElementsWithSpace(): String {
    return this.joinToString(separator = " ")
}

/**
 * Remove the latest character of given StringBuilder.
 */
fun StringBuilder.removeLastChar() {
    if (isNotEmpty())
        deleteCharAt(length - 1)
}
