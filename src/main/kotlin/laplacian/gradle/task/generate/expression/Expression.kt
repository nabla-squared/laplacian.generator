package laplacian.gradle.task.generate.expression

import com.github.jknack.handlebars.Context
import laplacian.util.*

class Expression(
    val expression: String
) : ExpressionProcessor {

    override lateinit var next: ExpressionProcessor

    override fun eval(path: String, context: Context) {
        val component =
            if (PH.containsMatchIn(expression)) {
                expression.handlebarsForPath().apply(context)
            }
            else {
                expression
            }
        next.eval(path + component, context)
    }

    companion object {
        val PH = """\{[^}]+}""".toRegex()
    }
}
