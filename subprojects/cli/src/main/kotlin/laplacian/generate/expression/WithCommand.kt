package laplacian.generate.expression

import com.github.jknack.handlebars.Context
import laplacian.generate.ExecutionContext

class WithCommand(
    private val valueExpr: String,
    private val varName: String,
    private val executionContext: ExecutionContext,
): ExpressionProcessor {

    override lateinit var next: ExpressionProcessor

    override fun eval(path: String, context: Context) {
        val value: Any? = VarExpression(valueExpr, executionContext).eval(context)
        val newContext = Context.newContext(context,mapOf(
            varName to value
        ))
        next.eval(path, newContext)
    }
}
