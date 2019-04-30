package laplacian.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class YamlLoaderTest {

    private fun yamlFile(content: String): File {
        val file = File.createTempFile(YamlLoaderTest::class.java.name, ".yml")
        file.writeText(content.trimMargin())
        file.deleteOnExit()
        return file
    }

    @Test
    fun it_unmarshals_a_map_object_from_a_yaml_file() {
        val yaml = yamlFile("""
        |data:
        |  - hoge
        |  - fuga
        |  - piyo
        """)

        val actual:Map<String, Any> = YamlLoader.readObjects(listOf(yaml))
        val expect = mapOf("data" to listOf("hoge", "fuga", "piyo"))

        assertEquals(expect, actual)
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
        val actual:Map<String, Any> = YamlLoader.readObjects(listOf(json))
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

        val actual:Map<String, Any> = YamlLoader.readObjects(listOf(yaml1, yaml2))
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

        val actual:Map<String, Any> = YamlLoader.readObjects(listOf(yaml1, yaml2))
        val expect = mapOf(
            "data1" to listOf("hoge1", "fuga1", "piyo1", "hoge2", "fuga2", "piyo2")
        )
        assertEquals(expect, actual)
    }
}
