package laplacian.handlebars.helper

import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class DefineHelper: Helper<Any> {
    override fun apply(context: Any?, options: Options): Any {
        val path: String
        val value: Any?
        if (context == null) throw java.lang.IllegalArgumentException(
            "Invalid #define tag: variable name must not be null."
        )
        val buffer = when (options.tagType) {
            TagType.VAR -> {
                path = context.toString()
                value = options.params[0]
                ""
            }
            TagType.SECTION -> {
                path = context.toString()
                value = options.fn()
                options.buffer()
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
        val name = path.replace("../", "")
        val c = if (path.startsWith("../")) options.context.parent() else options.context
        c.combine(name, value)
        return buffer
    }
    companion object {
        val INSTANCE = DefineHelper()
    }
}
