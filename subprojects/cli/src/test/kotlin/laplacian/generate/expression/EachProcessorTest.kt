package laplacian.generate.expression

import com.fasterxml.jackson.databind.ObjectMapper
import laplacian.generate.ExecutionContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class EachProcessorTest {

    @Test
    fun if_it_contains_each_command_it_is_expanded_to_multiple_paths() {
        val model = mapOf("persons" to listOf("hogeo", "fugami", "piyosuke"))
        val expr = "{each persons as person}{person}.json"

        val expect = listOf("hogeo.json", "fugami.json", "piyosuke.json")
        val context = ExecutionContext().apply {
            addModel(model)
            build()
        }
        val actual = ExpressionProcessor.process(expr, context).map{it.first}
        assertEquals(expect, actual)
    }

    fun null_values_should_be_treated_as_empty_lists_when_they_are_iterated() {
        val model = mapOf("persons" to listOf("hogeo", "fugami", "piyosuke"))
        val expr = "{each people as person}{person}.json"
        val expect = emptyList<String>()
        val context = ExecutionContext().apply {
            addModel(model)
            build()
        }
        val actual = ExpressionProcessor.process(expr, context).map{it.first}
        assertEquals(expect, actual)
    }

    fun error_should_be_thrown_if_items_actually_was_not_an_iterable_or_an_array() {
        val model = mapOf("count" to 3)
        val expr = "{each count as num}{num}.json"
        val context = ExecutionContext().apply{
            addModel(model)
            build()
        }
        assertThrows(RuntimeException::class.java) {
            ExpressionProcessor.process(expr, context)
        }
    }

    @Test
    fun error_should_be_thrown_if_variable_name_is_not_given() {
        val model = mapOf("persons" to listOf("hogeo", "fugami", "piyosuke"))
        val expr = "{each persons}{person}.json"
        val context = ExecutionContext().apply {
            addModel(model)
            build()
        }
        assertThrows(IllegalArgumentException::class.java) {
            ExpressionProcessor.process(expr, context)
        }
    }

    @Test
    fun if_a_path_have_multiple_each_commands_they_should_be_treated_as_nested_loop() {
        val feature1 = mapOf("name" to "FEATURE1", "pages" to listOf(
            mapOf("name" to "PAGE1"),
            mapOf("name" to "PAGE2"),
            mapOf("name" to "PAGE3")
        ))
        val feature2 = mapOf("name" to "FEATURE2", "pages" to listOf(
            mapOf("name" to "PAGE1"),
            mapOf("name" to "PAGE2")
        ))
        val model = mapOf("project" to mapOf("features" to listOf(feature1, feature2)))
        val expr = "/src/components/" +
                   "{each project.features as feature}{feature.name}/" +
                   "page/" +
                   "{each feature.pages as page}{page.name}-page.tsx"

        val expected = listOf(
            "/src/components/FEATURE1/page/PAGE1-page.tsx",
            "/src/components/FEATURE1/page/PAGE2-page.tsx",
            "/src/components/FEATURE1/page/PAGE3-page.tsx",
            "/src/components/FEATURE2/page/PAGE1-page.tsx",
            "/src/components/FEATURE2/page/PAGE2-page.tsx"
        )
        val context = ExecutionContext().apply {
            addModel(model)
            build()
        }
        val actual = ExpressionProcessor.process(expr, context).map{it.first}
        assertEquals(expected, actual)
    }
}
