package laplacian.handlebars.helper

import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class StringifyHelper<T>(private val fn: (target: T, opts: Options) -> String): Helper<T> {
    override fun apply(context: T, options: Options): Any {
        return when (options.tagType) {
            TagType.VAR, TagType.SUB_EXPRESSION -> {
                fn(context, options)
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
    }
}
