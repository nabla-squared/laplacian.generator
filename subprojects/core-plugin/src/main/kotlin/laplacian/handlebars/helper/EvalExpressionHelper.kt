package laplacian.handlebars.helper

import java.io.IOException
import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import laplacian.util.*
import laplacian.handlebars.*

class EvalExpressionHelper : Helper<Any> {

    val KEY: String = "EVALUATED_VALUE"
    @Throws(IOException::class)
    override fun apply(context: Any?, options: Options): Any? {
        if (context == null) throw IllegalArgumentException(
            "Expression must not be null."
        )
        val expression = context.toString()
        return options.context.evalExpression(expression)
    }

    companion object {
        /**
         * A singleton instance of this helper.
         */
        val INSTANCE: Helper<Any> = EvalExpressionHelper()

        /**
         * The helper's name.
         */
        val NAME = "eval-expression"
    }
}
