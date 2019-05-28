package laplacian.gradle.task.generate

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertAll

class ExecutionContextTest {
    @Test
    fun create_context() {
        val model = ExecutionContext().addModel("""
        |entities:
        |- name: programming_language
        |  properties:
        |  - name: name
        |    type: string
        |    primary_key: true
        |  - name: interpreter
        |    type: boolean
        """.trimMargin()).build().currentModel
        val entities: List<Map<String, Any?>> = model["entities"] as List<Map<String, Any?>>
        assertAll(
            { assertEquals("programming_language", entities[0]["name"]) },
            { assertTrue(entities[0]["properties"] is List<*>) }
        )
        val properties = entities[0]["properties"] as List<Map<String, Any?>>
        assertAll(
            { assertEquals(2, properties.size) },
            { assertEquals("name",properties[0]["name"]) },
            { assertEquals("string",properties[0]["type"]) },
            { assertTrue(properties[0]["primary_key"] as Boolean) }
        )
    }
}
