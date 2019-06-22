package laplacian.handlebars.helper

import java.io.IOException

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import java.lang.IllegalArgumentException

/**
 * Lookup helper, which allows to get a context variable.
 *
 * It was introduced with dynamic partials:
 *
 * <pre>
 * {{&gt; (lookup '.' 'myVariable') }}
</pre> *
 *
 * @author edgar
 * @since 2.2.0
 */
class LookupHelper : Helper<Any> {

    @Throws(IOException::class)
    override fun apply(context: Any?, options: Options): Any? {
        if (context == null) {
            return null
        }
        if (options.params.isEmpty()) throw IllegalArgumentException(
            "A lookup key is needed."
        )
        val ctx = Context.newBuilder(options.context, context).build()
        return ctx.get(options.param<Any>(0).toString())
    }

    companion object {

        /**
         * A singleton instance of this helper.
         */
        val INSTANCE: Helper<Any> = LookupHelper()

        /**
         * The helper's name.
         */
        val NAME = "lookup"
    }
}
