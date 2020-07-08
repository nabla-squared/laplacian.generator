package laplacian.handlebars.helper
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class ConcatHelper : Helper<Any> {

    @Throws(IllegalArgumentException::class)
    override fun apply(context: Any?, options: Options): Any {
        val params = listOf(context, *options.params)
        val toList = params.any{ it is Array<*> || it is Iterable<*>}
        when (options.tagType) {
            TagType.VAR, TagType.SUB_EXPRESSION -> {
                return if (toList) {
                    params.map{ ListHelper.asList(it) }.flatten()
                }
                else {
                    params.map{ it?.toString() ?: "" }.joinToString("")
                }
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
    }

    companion object {
        val INSTANCE: Helper<Any> = ConcatHelper()
    }
}
