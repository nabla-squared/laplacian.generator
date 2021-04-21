package laplacian.generate.expression

import com.github.jknack.handlebars.Context
import laplacian.generate.ExecutionContext
import laplacian.generate.util.handlebarsForPath

class Expression(
    val expression: String,
    val executionContext: ExecutionContext,
) : ExpressionProcessor {

    override lateinit var next: ExpressionProcessor

    override fun eval(path: String, context: Context) {
        val component =
            if (PH.containsMatchIn(expression)) {
                expression
                    .handlebarsForPath(executionContext.handlebarsHelpers)
                    .apply(context)
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
