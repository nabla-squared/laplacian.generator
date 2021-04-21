package laplacian.generate.expression

import com.github.jknack.handlebars.Context
import laplacian.generate.ExecutionContext

class IfCommand(
    val conditionExpr: String,
    val negativeFlg: Boolean = false,
    val executionContext: ExecutionContext,
): ExpressionProcessor {

    override lateinit var next: ExpressionProcessor

    override fun eval(path: String, context: Context) {
        val condition: Any? = VarExpression(conditionExpr, executionContext).eval(context)
        if (negativeFlg) {
            if (!truthy(condition)) {
                next.eval(path, context)
            }
        }
        else {
            if (truthy(condition)) {
                next.eval(path, context)
            }
        }
    }

    private fun truthy(v: Any?): Boolean {
        if (v == null) return false
        return when (v) {
            is Boolean -> v
            is Number -> v.toInt() != 0
            is Collection<*> -> !v.isEmpty()
            is CharSequence -> !v.isEmpty()
            else -> true
        }
    }
}
