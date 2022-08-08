package io.github.janbarari.gradle.analytics.domain.model.report

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import io.github.janbarari.gradle.extension.isNotNull
import org.gradle.internal.impldep.com.google.gson.JsonParser
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParallelExecutionRateReportJsonAdapterTest {

    lateinit var moshi: Moshi
    lateinit var adapter: ParallelExecutionRateReportJsonAdapter

    @BeforeAll
    fun setup() {
        moshi = Moshi.Builder().build()
        adapter = ParallelExecutionRateReportJsonAdapter(moshi)
    }

    @Test
    fun `Check fromJson() returns valid data model with valid json`() {
        val json = """
            {
                "medianValues": [
                    {
                        "value": 90,
                        "description": "20/10/2022"
                    }
                ],
                "suggestedMaxValue": 300,
                "suggestedMinValue": 0,
                
                "test-skipping-un-valid-field": true
            }
        """.trimIndent()

        val fromReader = adapter.fromJson(
            JsonReader.of(
                okio.Buffer().writeUtf8(json)
            )
        )
        assertTrue {
            fromReader.isNotNull() && fromReader.medianValues[0].value == 90L &&
                    fromReader.suggestedMinValue == 0L && fromReader.suggestedMaxValue == 300L
        }
    }

    @Test
    fun `Check fromJson() throws exception with unValid json`() {
        assertThrows<JsonEncodingException> {
            val json = """
            {
                Is is fake json
            }
        """.trimIndent()
            adapter.fromJson(
                JsonReader.of(
                    okio.Buffer().writeUtf8(json)
                )
            )
        }
        assertThrows<JsonDataException> {
            val json = """
                {
                    "medianValues": null
                }
            """.trimIndent()
            adapter.fromJson(
                JsonReader.of(
                    okio.Buffer().writeUtf8(json)
                )
            )
        }
    }

    @Test
    fun `Check toString() is not empty`() {
        assertTrue {  adapter.toString().isNotEmpty() }
    }

    @Test
    fun `Check toJson() throws NullPointerException with unValid data model`() {
        val unValidModel = null
        assertThrows<NullPointerException> {
            adapter.toJson(unValidModel)
        }
    }

    @Test
    fun `Check toJson() return valid Json with valid data model`() {
        val validModel = ParallelExecutionRateReport(emptyList(), 300, 0)
        assertDoesNotThrow {
            JsonParser.parseString(adapter.toJson(validModel))
        }
    }

}