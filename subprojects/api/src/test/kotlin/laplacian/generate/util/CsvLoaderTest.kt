package laplacian.generate.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.IllegalStateException
import java.nio.file.Files

class CsvLoaderTest {

    private val tempDir = Files.createTempDirectory(null).toFile().also {
        it.deleteOnExit()
    }

    private fun file(filename: String, content: String): File {
        val file = File(tempDir, filename)
        file.writeText(content.trimMargin())
        file.deleteOnExit()
        return file
    }

    @Test
    fun it_validates_content_of_csv_using_json_schema() {
        val jsonSchema = file("schema.json", """
        |{
        |  "type": "object",
        |  "properties": {
        |    "data": {
        |      "type": "array",
        |      "items": {
        |        "type": "object",
        |        "properties": {
        |          "number-column": { type: "number", enum: [1, 2, 3, 4] },
        |          "string-column": { type: "string" }
        |        }
        |      }
        |    }
        |  }
        |}
        """.trimIndent())
        val invalidDataFile = file("invalid__data.csv", """
        |number-column@number,string-column
        |1,OK
        |2,OK
        |42,invalid!!
        |3,OK
        """.trimIndent())
        val validDataFile = file("valid__data.csv", """
        |number-column@number,string-column
        |1,OK!
        |2,OK!!
        |3,OK!!!
        """.trimIndent())
        assertThrows(IllegalStateException::class.java) {
            CsvLoader.readObjects(listOf(invalidDataFile), jsonSchema)
        }
        val validData = CsvLoader.readObjects(listOf(validDataFile), jsonSchema)
        val expectedData = mapOf("data" to listOf(
            mapOf("number-column" to 1, "string-column" to "OK!"),
            mapOf("number-column" to 2, "string-column" to "OK!!"),
            mapOf("number-column" to 3, "string-column" to "OK!!!"),
        ))
        assertEquals(expectedData, validData)
    }

    @Test
    fun it_unmarshalls_a_map_object_from_a_yaml_file() {
        val csv = file("data-file.csv", """
        |data
        |hoge
        |fuga
        |piyo
        """.trimIndent())
        val actual = CsvLoader.readObjects(listOf(csv)) as Map<String, List<String>>
        val expect = mapOf("data-file" to listOf(
            mapOf("data" to "hoge"),
            mapOf("data" to "fuga"),
            mapOf("data" to "piyo"),
        ))
        assertEquals(expect, actual)
    }

    @Test
    fun it_unmarshalls_a_merged_map_object_from_multiple_yaml_files() {
        val data1Csv = file("data1.csv", """
        |data
        |hoge1
        |fuga1
        |piyo1
        """.trimIndent())
        val data2Csv = file("data2.csv","""
        |data
        |hoge2
        |fuga2
        |piyo2
        """.trimIndent())
        val actual = CsvLoader.readObjects(listOf(data1Csv, data2Csv))
        val expect = mapOf(
            "data1" to listOf(mapOf("data" to "hoge1"), mapOf("data" to "fuga1"), mapOf("data" to "piyo1")),
            "data2" to listOf(mapOf("data" to "hoge2"), mapOf("data" to "fuga2"), mapOf("data" to "piyo2")),
        )
        assertEquals(expect, actual)
    }

    @Test
    fun it_map_are_deep_merged_and_arrays_are_added_not_simply_replaced() {
        val testDataCsv = file("20201111__test-data.csv", """
        |data
        |hoge1
        |fuga1
        |piyo1
        """.trimIndent())
        val additionalDataCsv = file("20201212__test-data.csv", """
        |data
        |hoge2
        |fuga2
        |piyo2
        """.trimIndent())
        val actual = CsvLoader.readObjects(listOf(testDataCsv, additionalDataCsv))
        val expect = mapOf(
            "test-data" to listOf(
                mapOf("data" to "hoge1"),
                mapOf("data" to "fuga1"),
                mapOf("data" to "piyo1"),
                mapOf("data" to "hoge2"),
                mapOf("data" to "fuga2"),
                mapOf("data" to "piyo2"),
            )
        )
        assertEquals(expect, actual)
    }
}
