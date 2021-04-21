package laplacian.handlebars.helper

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options

class JoinHelper: Helper<Any> {
    override fun apply(context: Any, options: Options): Any {
        if (context !is Iterable<*>) throw IllegalArgumentException(
           "block-join only accepts an iterable but was: ${context.javaClass.canonicalName}"
        )
        val items = context.toList()
        val parent = options.context
        val fn = options.fn
        val separator = options.hash.getOrDefault("separator", ",")
        return items.mapIndexed { index, item ->
            val itCtx = Context.newContext(parent, item)
            val even = (index % 2 == 0)
            itCtx.combine("@key", index)
                 .combine("@index", index)
                 .combine("@first", if (index == 0) "first" else  "")
                 .combine("@last", if (index >= items.size-1) "" else "last")
                 .combine("@odd", if (even) "" else "odd")
                 .combine("@even", if (even) "even" else "")
                 .combine("@index_1", index + 1)
            options.apply(fn, itCtx, listOf(item, index)).trimEnd()
        }
        .filter{ it.isNotEmpty() }
        .distinct()
        .joinToString(separator.toString())
    }

    companion object {
        val INSTANCE = JoinHelper()
    }
}
