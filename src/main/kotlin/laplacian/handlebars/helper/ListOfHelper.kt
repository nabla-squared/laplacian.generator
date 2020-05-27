package laplacian.handlebars.helper

import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class ListOfHelper : Helper<Any?> {

    @Throws(IllegalArgumentException::class)
    override fun apply(context: Any?, options: Options): List<Any?> {
        return when (options.tagType) {
            TagType.VAR, TagType.SUB_EXPRESSION -> {
                if (context == null && options.params.isEmpty()) emptyList()
                else listOf(context) + options.params
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
    }

    companion object {
        val INSTANCE: Helper<Any?> = ListOfHelper()
    }
}

