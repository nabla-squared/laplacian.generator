package laplacian.gradle.task.generate.expression

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class IfProcessorTest {

    @Test
    fun it_do_nothing_if_its_condition_is_true() {
        val model = mapOf("use_json" to true)
        val expr = "{if use_json}data.json"
        val expect = listOf("data.json")
        val actual = ExpressionProcessor.process(expr, model).map{it.first}
        assertEquals(expect, actual)
    }

    @Test
    fun it_prevents_to_generate_a_path_if_its_condition_is_falsy() {
        val model = mapOf("use_json" to false)
        val expr = "{if use_json}data.json"
        val expect = emptyList<String>()
        val actual = ExpressionProcessor.process(expr, model).map{it.first}
        assertEquals(expect, actual)
    }

    @Test
    fun it_effects_conversely_when_the_keyword_unless_is_used_instead_of_if() {
        val model = mapOf("use_json" to true)
        val expr = "{unless use_json}data.json"
        val expect = emptyList<String>()
        val actual = ExpressionProcessor.process(expr, model).map{it.first}
        assertEquals(expect, actual)
    }
}
