package laplacian.generate.expression

import com.github.jknack.handlebars.Context
import laplacian.generate.ExecutionContext

class EachCommand(
    private val itemsExpr: String,
    private val varName: String,
    private val executionContext: ExecutionContext,
): ExpressionProcessor {

    override lateinit var next: ExpressionProcessor

    override fun eval(path: String, context: Context) {
        val items: Any? = VarExpression(itemsExpr, executionContext).eval(context)
        val list = when {
            (items is Array<*>) -> listOf(*items)
            (items is List<*>) -> items
            else -> emptyList()
            /*
            else -> throw IllegalStateException(
                "$itemsExpr should be an array or list but was: ${items?.javaClass?.name ?: "null"}"
            )
            */
        }
        list.filterNotNull().forEachIndexed { index, item ->
            val newContext = Context.newContext(context, mapOf(
                varName to item,
                "@index" to index,
                "@first" to (index == 0),
                "@last" to (index == list.lastIndex)
            ))
            next.eval(path, newContext)
        }
    }
}
