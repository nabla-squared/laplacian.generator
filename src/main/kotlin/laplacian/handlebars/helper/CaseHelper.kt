package laplacian.handlebars.helper
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType

class CaseHelper : Helper<Any> {

    @Throws(IllegalArgumentException::class)
    override fun apply(context: Any?, options: Options): Any? {
        val params = listOf(context, *options.params)
        if (options.params.size == 0) throw IllegalArgumentException(
            "Invalid parameter(s): $params. Usage: ${CaseHelper.USAGE}"
        )
        when (options.tagType) {
            TagType.VAR, TagType.SUB_EXPRESSION -> {
                params.chunked(2).forEach { pair ->
                    val condition = pair.first()
                    val value = pair.last()
                    if (pair.size == 1) return value // ELSE_VALUE
                    val truthy = condition != null && !options.isFalsy(condition)
                    if (truthy) return pair.last()
                }
                return null
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
    }

    companion object {
        val INSTANCE: Helper<Any> = CaseHelper()
        val USAGE = "{{case CONDITION_1 VALUE_1 CONDITION_2 VALUE_2 ... ELSE_VALUE}}"
    }
}
