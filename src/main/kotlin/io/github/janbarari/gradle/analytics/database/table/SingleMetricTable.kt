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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.database.table

import io.github.janbarari.gradle.analytics.database.Database
import io.github.janbarari.gradle.analytics.database.LongTextColumnType
import org.jetbrains.exposed.sql.Table

object SingleMetricTable : Table("single_metric") {

    /**
     * The unique auto-generated id.
     *
     * It also is the primary-key of the table.
     */
    val id = long("id").autoIncrement().uniqueIndex()

    val key = varchar("key", Database.DEFAULT_VARCHAR_LENGTH)

    val createdAt = long("created_at")

    val branch = varchar("branch", Database.DEFAULT_VARCHAR_LENGTH)

    val value = registerColumn<String>("value", LongTextColumnType())

    override val primaryKey = PrimaryKey(id)

}
