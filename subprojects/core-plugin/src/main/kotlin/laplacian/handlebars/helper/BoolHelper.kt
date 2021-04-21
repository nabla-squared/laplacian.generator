package laplacian.handlebars.helper

import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class BoolHelper(private val fn: (value: Any?, opts: Options) -> Boolean): Helper<Any> {
    override fun apply(context: Any?, options: Options): Any {
        return when (options.tagType) {
            TagType.VAR, TagType.SUB_EXPRESSION -> {
                fn(context, options)
            }
            TagType.SECTION -> {
                val buffer = options.buffer()
                val value = options.fn()
                buffer.append(fn(value, options).toString())
                buffer
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
    }
}
