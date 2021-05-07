package laplacian.handlebars.helper
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class IfHelper : Helper<Any> {

    @Throws(IllegalArgumentException::class)
    override fun apply(context: Any?, options: Options): Any? {
        val params = options.params
        val falsy = options.isFalsy(context)

        return when (options.tagType) {
            TagType.VAR, TagType.SUB_EXPRESSION -> {
                val alternate = if (params.size > 1) params[1]
                                else ""
                when {
                   (context == null || falsy) -> alternate
                   (params.isNotEmpty()) -> params.first()
                   else -> context
                }
            }
            TagType.SECTION -> options.buffer().also {
                it.append(
                    if (falsy) options.inverse()
                    else options.fn()
                )
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
    }

    companion object {
        val INSTANCE: Helper<Any> = IfHelper()
    }
}
