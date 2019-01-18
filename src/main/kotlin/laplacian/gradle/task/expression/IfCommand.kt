package laplacian.gradle.task.expression

import com.github.jknack.handlebars.Context
import laplacian.util.*

class IfCommand(
    val itemsExpr: String,
    val negativeFlg: Boolean = false
): ExpressionProcessor {

    override lateinit var next: ExpressionProcessor

    override fun eval(path: String, context: Context) {
        val condition: Any? = context.get(itemsExpr)
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