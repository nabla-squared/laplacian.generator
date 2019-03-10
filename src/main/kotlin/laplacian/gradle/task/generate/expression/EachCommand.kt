package laplacian.gradle.task.generate.expression

import com.github.jknack.handlebars.Context
import java.lang.IllegalStateException

class EachCommand(
    private val itemsExpr: String,
    private val varName: String
): ExpressionProcessor {

    override lateinit var next: ExpressionProcessor

    override fun eval(path: String, context: Context) {
        val items: Any? = VarExpression(itemsExpr).eval(context)
        val list = when {
            (items is Array<*>) -> listOf(*items)
            (items is List<*>) -> items
            else -> throw IllegalStateException(
                "$itemsExpr should be an array or list but was: ${items?.javaClass?.name ?: "null"}"
            )
        }
        list.filter{ it != null }.forEachIndexed { index, item ->
            next.eval(
                path,
                Context.newContext(context, mapOf(
                    varName to item,
                    "@index" to index,
                    "@first" to (index == 0),
                    "@last" to (index == list.lastIndex)
                ))
            )
        }
    }
}
