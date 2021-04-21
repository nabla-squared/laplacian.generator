package laplacian.generate.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.IllegalStateException

class YamlLoaderTest {

    private fun yamlFile(content: String): File {
        val file = File.createTempFile(YamlLoaderTest::class.java.name, ".yml")
        file.writeText(content.trimMargin())
        file.deleteOnExit()
        return file
    }

    @Test
    fun it_validates_yaml_using_json_schema() {
        val jsonSchema = yamlFile("""
        |{
        |  "type": "object",
        |  "properties": {
        |    "number": { type: "number", enum: [1, 2, 3, 4] }
        |  }
        |}
        """.trimIndent())
        val invalid = yamlFile("{number: 42}")
        val valid = yamlFile("{number: 3}")
        assertThrows(IllegalStateException::class.java) {
            YamlLoader.readObjects(listOf(invalid), jsonSchema)
        }
        assertEquals(3, YamlLoader.readObjects(listOf(valid), jsonSchema)["number"])
    }

    @Test
    fun it_unmarshals_a_map_object_from_a_yaml_file() {
        val yaml = yamlFile("""
        |data:
        |  - hoge
        |  - fuga
        |  - piyo
        """.trimIndent())

        val actual:Map<String, Any?> = YamlLoader.readObjects(listOf(yaml))
        val expect = mapOf("data" to listOf("hoge", "fuga", "piyo"))

        assertEquals(expect, actual)
    }

    @Test
    fun value_references_can_be_used() {
        val yaml = yamlFile("""
        |common_props: &common_props
        |  common_prop1: v1
        |  common_prop2: v2
        |
        |data:
        |  <<: *common_props
        |  additional_prop: a
        |
        """.trimIndent())

        val actual:Map<String, Any?> = YamlLoader.readObjects(listOf(yaml))
        val expect = mapOf("common_prop1" to "v1", "common_prop2" to "v2", "additional_prop" to "a")
        assertEquals(expect, actual["data"])
    }


    @Test
    fun it_unmarshals_objects_from_json_files() {
        val json = yamlFile(
            """|{
               |  data: [
               |    "hoge",
               |    "fuga",
               |    "piyo"
               |  ]
               |}""".trimMargin())
        val actual:Map<String, Any?> = YamlLoader.readObjects(listOf(json))
        val expect = mapOf("data" to listOf("hoge", "fuga", "piyo"))
        assertEquals(expect, actual)
    }

    @Test
    fun it_unmarshals_a_merged_map_object_from_multiple_yaml_files() {
        val yaml1 = yamlFile("""
        |data1:
        |  - hoge1
        |  - fuga1
        |  - piyo1
        """)

        val yaml2 = yamlFile("""
        |data2:
        |  - hoge2
        |  - fuga2
        |  - piyo2
        """)

        val actual:Map<String, Any?> = YamlLoader.readObjects(listOf(yaml1, yaml2))
        val expect = mapOf(
            "data1" to listOf("hoge1", "fuga1", "piyo1"),
            "data2" to listOf("hoge2", "fuga2", "piyo2")
        )
        assertEquals(expect, actual)
    }

    @Test
    fun it_map_are_deep_merged_and_arrays_are_added_not_simply_replaced() {
        val yaml1 = yamlFile("""
        |data1:
        |  - hoge1
        |  - fuga1
        |  - piyo1
        """)

        val yaml2 = yamlFile("""
        |data1:
        |  - hoge2
        |  - fuga2
        |  - piyo2
        """)

        val actual:Map<String, Any?> = YamlLoader.readObjects(listOf(yaml1, yaml2))
        val expect = mapOf(
            "data1" to listOf("hoge1", "fuga1", "piyo1", "hoge2", "fuga2", "piyo2")
        )
        assertEquals(expect, actual)
    }
}
