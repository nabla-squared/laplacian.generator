package laplacian.generate.expression

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Template
import laplacian.generate.ExecutionContext
import laplacian.generate.util.handlebars

class VarExpression(
    val expression: String,
    val executionContext: ExecutionContext,
) {
    private val template: Template? =
        if (expression.startsWith("(")) {
            """{{define "$TEMP_VAR_NAME" $expression }}""".handlebars(
                helpers = executionContext.handlebarsHelpers
            )
        }
        else { null }

    fun eval(context: Context): Any? {
        if (template == null) {
            return context.get(expression)
        }
        template.apply(context)
        return context.get(TEMP_VAR_NAME)
    }
    companion object {
        const val TEMP_VAR_NAME = "laplacianSubexpressionTempValue"
    }
}
