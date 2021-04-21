package laplacian.generate.expression

import com.github.jknack.handlebars.Helper
import laplacian.generate.ExecutionContext
import laplacian.handlebars.HandlebarsExtension
import laplacian.generate.util.lowerUnderscorize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ExpressionProcessorTest {

    @Test
    fun it_processes_expressions_in_a_path() {
        val model = mapOf("person" to mapOf("name" to "Hogeta-Hogeo"))
        val context = ExecutionContext().apply {
            addModel(model)
            build()
        }
        assertEquals(
            listOf("/Hogeta-Hogeo.json"),
            ExpressionProcessor.process(
                "/{person.name}.json", context
            ).map{it.first}
        )
        assertEquals(
            listOf("/hoge/fuga/Hogeta-Hogeo.json"),
            ExpressionProcessor.process(
                "/hoge/fuga/{person.name}.json", context
            ).map{it.first}
        )
    }

    @Test
    fun it_processes_expressions_with_helper() {
        val model = mapOf("person" to mapOf("name" to "Hogeta-Hogeo"))
        val expr = "/hoge/fuga/{lower-snake person.name}.json"
        val expect = listOf("/hoge/fuga/hogeta_hogeo.json")
        val context = ExecutionContext().apply{
            addModel(model)
            addHandlebarsExtension(object: HandlebarsExtension {
                override fun handlebarHelpers(): Map<String, Helper<*>> = mapOf(
                    "lower-snake" to Helper<String>{ s, _ -> s.lowerUnderscorize() }
                )
            })
            build()
        }
        val actual = ExpressionProcessor.process(expr, context).map{it.first}
        assertEquals(expect, actual)
    }

}
