package laplacian.generate.expression

import laplacian.generate.ExecutionContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class WithProcessorTest {

    @Test
    fun defines_new_value_on_context() {
        val model = mapOf("person" to mapOf(
            "name" to "hogeo",
            "age" to 24
        ))
        val expr = "{with person.name as name}{name}.json"
        val expect = listOf("hogeo.json")
        val context = ExecutionContext().apply {
            addModel(model)
            build()
        }
        val actual = ExpressionProcessor.process(expr, context).map{it.first}
        assertEquals(expect, actual)
    }
}
