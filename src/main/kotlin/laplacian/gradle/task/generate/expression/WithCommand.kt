package laplacian.gradle.task.generate.expression

import com.github.jknack.handlebars.Context
import java.lang.IllegalStateException

class WithCommand(
    private val valueExpr: String,
    private val varName: String
): ExpressionProcessor {

    override lateinit var next: ExpressionProcessor

    override fun eval(path: String, context: Context) {
        val value: Any? = VarExpression(valueExpr).eval(context)
        next.eval(
            path,
            Context.newContext(context, mapOf(
                varName to value
            ))
        )
    }
}
