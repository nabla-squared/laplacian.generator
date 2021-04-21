package laplacian.generate.expression

import laplacian.generate.ExecutionContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class IfProcessorTest {

    @Test
    fun it_do_nothing_if_its_condition_is_true() {
        val model = mapOf("use_json" to true)
        val expr = "{if use_json}data.json"
        val expect = listOf("data.json")
        val context = ExecutionContext().apply {
            addModel(model)
            build()
        }
        val actual = ExpressionProcessor.process(expr, context).map{it.first}
        assertEquals(expect, actual)
    }

    @Test
    fun it_prevents_to_generate_a_path_if_its_condition_is_falsy() {
        val model = mapOf("use_json" to false)
        val expr = "{if use_json}data.json"
        val expect = emptyList<String>()
        val context = ExecutionContext().apply {
            addModel(model)
            build()
        }
        val actual = ExpressionProcessor.process(expr, context).map{it.first}
        assertEquals(expect, actual)
    }

    @Test
    fun it_affects_conversely_when_the_keyword_unless_is_used_instead_of_if() {
        val model = mapOf("use_json" to true)
        val expr = "{unless use_json}data.json"
        val expect = emptyList<String>()
        val context = ExecutionContext().apply {
            addModel(model)
            build()
        }
        val actual = ExpressionProcessor.process(expr, context).map{it.first}
        assertEquals(expect, actual)
    }

    @Test
    fun it_allows_to_use_a_subexpression_as_a_condition() {
        val model = mapOf("use_json" to true)
        val expr = "{if (not use_json)}data.json"
        val expect = emptyList<String>()
        val context = ExecutionContext().apply {
            addModel(model)
            build()
        }
        val actual = ExpressionProcessor.process(expr, context).map{it.first}
        assertEquals(expect, actual)
    }
}
