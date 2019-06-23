package laplacian.handlebars.helper
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class IfHelper : Helper<Any> {

    override fun apply(context: Any, options: Options): Any {
        val params = options.params
        val falsy = options.isFalsy(context)
        val value = if (params.size > 0) params.first()
                    else context
        val alternate = if (params.size > 1) params[1]
                        else ""
        return when (options.tagType) {
            TagType.VAR -> if (falsy) alternate
                           else value
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
