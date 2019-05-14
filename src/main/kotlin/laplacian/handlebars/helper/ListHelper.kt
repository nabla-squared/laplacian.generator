package laplacian.handlebars.helper

import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class ListHelper(private val fn: (list: List<Any?>, opts: Options) -> List<Any?>): Helper<Any> {
    override fun apply(context: Any?, options: Options): Any {
        return when (options.tagType) {
            TagType.VAR, TagType.SUB_EXPRESSION -> {
                fn(asList(context), options)
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
    }
    companion object {
        fun asList(obj: Any?): List<Any?> {
            return when {
                (obj == null) -> emptyList()
                (obj is List<*>) -> obj
                (obj is Array<*>) -> obj.toList()
                (obj is Iterable<*>) -> obj.toList()
                else -> listOf(obj)
            }
        }
    }
}
