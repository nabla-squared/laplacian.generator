package laplacian.gradle.task.expression

import com.github.jknack.handlebars.Context
import laplacian.util.*

class EachCommand(
    val itemsExpr: String,
    val varName: String
): ExpressionProcessor {

    override lateinit var next: ExpressionProcessor

    override fun eval(path: String, context: Context) {
        val items: Any? = context.get(itemsExpr)
        val list =
            if (items is Array<*>) {
                listOf(*items)
            }
            else if (items is List<*>) {
                items
            }
            else throw IllegalArgumentException(
                "${itemsExpr} should be an array or list but was: ${items?.javaClass?.name ?: "null"}"
            )
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
