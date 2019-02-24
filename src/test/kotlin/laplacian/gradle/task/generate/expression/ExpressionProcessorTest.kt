package laplacian.gradle.task.generate.expression

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ExpressionProcessorTest {

    @Test
    fun it_processes_expressions_in_a_path() {
        val model = mapOf("person" to mapOf("name" to "Hogeta-Hogeo"))
        val expr = "/hoge/fuga/{person.name}.json"

        val expect = listOf("/hoge/fuga/Hogeta-Hogeo.json")
        val actual = ExpressionProcessor.process(expr, model).map{it.first}
        assertEquals(expect, actual)
    }
}
