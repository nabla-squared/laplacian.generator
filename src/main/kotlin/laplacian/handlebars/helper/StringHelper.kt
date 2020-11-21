package laplacian.handlebars.helper

import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class StringHelper(private val fn: (str: String, opts: Options) -> String): Helper<Any> {
    override fun apply(context: Any?, options: Options): Any {
        return when (options.tagType) {
            TagType.VAR, TagType.SUB_EXPRESSION -> {
                fn(context?.toString() ?: "", options)
            }
            TagType.SECTION -> {
                val buffer = options.buffer()
                val str = options.fn()
                buffer.append(fn(str.toString(), options))
                buffer
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
    }
}
