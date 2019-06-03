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
        val m = PATH_EXPR.find(path) ?: throw IllegalArgumentException(
            "Invalid variable expression: $path"
        )
        val upperScope = m.groupValues[1].length / 3
        val name = m.groupValues[2]
        val targetContext = (1..upperScope).fold(options.context) { c, _ ->
             c.parent()
        }
        targetContext.combine(name, value)
        return buffer
    }
    companion object {
        val PATH_EXPR = """^((?:\.\./)*)([-_a-zA-Z0-9$]+)""".toRegex()
        val INSTANCE = DefineHelper()
    }
}
