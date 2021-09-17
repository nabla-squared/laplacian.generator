package laplacian.handlebars.helper

import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class SplitHelper : Helper<Any?> {

    @Throws(IllegalArgumentException::class)
    override fun apply(context: Any?, options: Options): List<Any?> {
        return when (options.tagType) {
            TagType.VAR, TagType.SUB_EXPRESSION -> {
                if (context == null) return emptyList()
                val tokens = context.toString()
                val separator = options.params[0]
                if (separator == null) return listOf(tokens)
                return tokens.split(separator.toString())
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
    }

    companion object {
        val INSTANCE: Helper<Any?> = SplitHelper()
    }
}

