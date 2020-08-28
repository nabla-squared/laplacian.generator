package laplacian.gradle.task.generate.expression

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
        val actual = ExpressionProcessor.process(expr, model).map{it.first}
        assertEquals(expect, actual)
    }
}
