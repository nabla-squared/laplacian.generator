package laplacian.handlebars.helper

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import java.lang.IllegalArgumentException

class ContainsKeyHelper : Helper<Any> {

    override fun apply(context: Any?, options: Options): Any? {
        if (context == null) {
            return null
        }
        if (options.params.size <= 0) {
            return context
        }
        val key = options.param<Any>(0)?.toString() ?: throw IllegalArgumentException(
            "The contains-key helper always requires the second parameter."
        )
        return when(context) {
            is Iterable<*> -> context.toList().getOrNull(key.toInt()) ?: false
            is Array<*> -> context.getOrNull(key.toInt()) ?: false
            is Map<*, *> -> context.containsKey(key)
            else -> Context.newBuilder(options.context, context).build().get(key) ?: false
        }
    }

    companion object {
        val INSTANCE: Helper<Any> = ContainsKeyHelper()
    }
}
