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
package io.github.janbarari.gradle.analytics.plugin.config

import io.github.janbarari.gradle.analytics.data.database.config.DatabaseConfig
import io.github.janbarari.gradle.analytics.data.database.config.MySqlDatabaseConfig
import io.github.janbarari.gradle.analytics.data.database.config.SqliteDatabaseConfig

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
class DatabaseExtension : java.io.Serializable {

    /**
     * It is the database config of user local machine, this variable should be initialized
     * with one of the database configs that the plugin supports.
     */
    var local: DatabaseConfig? = null

    /**
     * It is the database config of CI. Should be initialized
     * with one of the database configs that the plugin supports. Keep in mind that
     * if this variable is initialized, then the plugin only uses this database
     * config on CI.
     *
     * Note: please make sure the CI has an environment variable named `CI`.
     */
    var ci: DatabaseConfig? = null

    /**
     * Factory method for create a new instance
     * of [io.github.janbarari.gradle.analytics.data.database.config.MySqlDatabaseConfig].
     */
    fun mysql(block: MySqlDatabaseConfig.() -> Unit): MySqlDatabaseConfig {
        return MySqlDatabaseConfig {
            also(block)
        }
    }

    /**
     * Factory method for create a new instance
     * of [io.github.janbarari.gradle.analytics.data.database.config.SqliteDatabaseConfig].
     */
    fun sqlite(block: SqliteDatabaseConfig.() -> Unit): SqliteDatabaseConfig {
        return SqliteDatabaseConfig {
            also(block)
        }
    }

}
