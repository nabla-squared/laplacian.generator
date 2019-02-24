package laplacian.util

import com.github.jknack.handlebars.*
import com.github.jknack.handlebars.context.FieldValueResolver
import com.github.jknack.handlebars.context.JavaBeanValueResolver
import com.github.jknack.handlebars.context.MapValueResolver
import com.github.jknack.handlebars.context.MethodValueResolver
import com.github.jknack.handlebars.helper.ConditionalHelpers
import com.github.jknack.handlebars.helper.StringHelpers
import org.atteo.evo.inflector.English

val SEPARATOR = """([-./_\s]+|(?<=[a-z])(?=[A-Z]))""".toRegex()

fun splitIdentifierIntoTokens(str: String): List<String> = str.split(SEPARATOR)

fun String.upperCamelize() =
        splitIdentifierIntoTokens(this)
            .map{ it[0].toUpperCase() + it.substring(1) }
            .joinToString("")

fun String.lowerCamelize(): String {
    val camelized = this.upperCamelize()
    return camelized[0].toLowerCase() + camelized.substring(1)
}

fun joinTokens(str: String, separator: String, upperCase: Boolean): String {
    return splitIdentifierIntoTokens(str)
          .map{ if (upperCase) it.toUpperCase() else it.toLowerCase() }
          .joinToString(separator)
}

fun String.lowerUnderscorize() = joinTokens(this, "_", false)
fun String.upperUnderscorize() = joinTokens(this, "_", true)
fun String.lowerHyphenize() = joinTokens(this, "-", false)
fun String.upperHyphenize() = joinTokens(this, "-", true)
fun String.pathify() = joinTokens(this.replace("""[-_]+""".toRegex(), ""), "/", false)

fun String.stripBlankLines() =
        this.replace("""\n\s*\n""".toRegex(), "\n")
            .replace("""\s*\n+$""".toRegex(RegexOption.MULTILINE), "")
            .replace("""^\s*\n""".toRegex(RegexOption.MULTILINE), "")
val DOC_COMMENT = """(/\*\*[\s\S]*?\*+/|^\s*#.*$)""".toRegex(RegexOption.MULTILINE)

fun String.stripDocComments() =
        this.replace(DOC_COMMENT, "")
            .stripBlankLines()

fun String.pluralize() = English.plural(this)

class StringHelper(private val fn: (str: String, opts: Options) -> String): Helper<Any> {
    override fun apply(context: Any?, options: Options): Any {
        return when (options.tagType) {
            TagType.VAR, TagType.SUB_EXPRESSION -> {
                fn(context.toString(), options)
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

class DefineHelper: Helper<Any> {
    override fun apply(context: Any, options: Options): Any {
        val name: String
        val value: Any?
        val buffer = when (options.tagType) {
            TagType.VAR -> {
                name = context.toString()
                value = options.params[0]
                ""
            }
            TagType.SECTION -> {
                name = context.toString()
                value = options.fn()
                options.buffer()
            }
            else -> throw IllegalArgumentException(
                "Unsupported tag type: ${options.tagType}"
            )
        }
        options.context.combine(name, value)
        return buffer
    }
    companion object {
        val INSTANCE = DefineHelper()
    }
}

class JoinHelper: Helper<Any> {
    override fun apply(context: Any, options: Options): Any {
        if (context !is Iterable<*>) throw IllegalArgumentException(
           "block-join only accepts an iterable but was: ${context.javaClass.canonicalName}"
        )
        val items = context.toList()
        val parent = options.context
        val fn = options.fn
        val separator = options.hash.getOrDefault("separator", ",")
        return items.mapIndexed { index, item ->
            val itCtx = Context.newContext(parent, item)
            val even = (index % 2 == 0)
            itCtx.combine("@key", index)
                 .combine("@index", index)
                 .combine("@first", if (index == 0) "first" else  "")
                 .combine("@last", if (index >= items.size-1) "" else "last")
                 .combine("@odd", if (even) "" else "odd")
                 .combine("@even", if (even) "even" else "")
                 .combine("@index_1", index + 1)
            options.apply(fn, itCtx, listOf(item, index)).trimEnd()
        }
        .filter{ it.isNotEmpty() }
        .distinct()
        .joinToString(separator.toString())
    }

    companion object {
        val INSTANCE = JoinHelper()
    }
}

fun identifierHelper(wide: Boolean = false, fn: (str: String, opts: Options) -> String): Helper<Any> = StringHelper() { str, opts ->
    str.replace(if (wide) IDENTIFIER_TOKEN_WIDE else IDENTIFIER_TOKEN) { fn(it.value, opts) }
}

val IDENTIFIER_TOKEN = """[-_a-zA-Z][-_a-zA-Z0-9$]*""".toRegex()
val IDENTIFIER_TOKEN_WIDE = """[-_a-zA-Z][-./_a-zA-Z0-9$]*""".toRegex()

fun String.handlebars(): Template {
    val template = HANDLEBARS.compileInline(this)
    return TemplateWrapper(template)
}
val HANDLEBARS = createTemplate()

fun String.handlebarsForPath(): Template {
    val template = HANDLEBARS_FOR_PATH.compileInline(this)
    return TemplateWrapper(template)
}
val HANDLEBARS_FOR_PATH = createTemplate(true)

fun String.shift(shiftWidth: Int): String {
    val pad = "".padEnd(shiftWidth, ' ')
    return this.trim().replace("""\n([^\n]*)""".toRegex()) { m ->
        "\n${pad}${m.groupValues[1]}".trimEnd(' ')
    }
}

fun String.dquote(): String =
    "\"${this.trim().replace("""["\\]""".toRegex(), "\\\\$0")}\""

fun createTemplate(forPath: Boolean = false): Handlebars {
    val template = Handlebars()
        .with(EscapingStrategy.NOOP)
        .registerHelper("lower-camel", identifierHelper{t, _ -> t.lowerCamelize()})
        .registerHelper("upper-camel", identifierHelper{t, _ -> t.upperCamelize()})
        .registerHelper("hyphen", identifierHelper{t, _ -> t.lowerHyphenize()})
        .registerHelper("lower-underscore", identifierHelper{t, _ -> t.lowerUnderscorize()})
        .registerHelper("upper-underscore", identifierHelper{t, _ -> t.upperUnderscorize()})
        .registerHelper("path", StringHelper{t, _ -> t.pathify()})
        .registerHelper("plural", identifierHelper{t, _ -> t.pluralize()})
        .registerHelper("shift", StringHelper{ t, opts ->
            val width = opts.hash.getOrElse("width"){opts.params[0]} as Int
            t.shift(width)
        })
        .registerHelper("trim", StringHelper{ t, _ -> t.trim()})
        .registerHelper("dquote", StringHelper{ t, _ -> t.dquote()})
        .registerHelper("block-join", JoinHelper.INSTANCE)
        .registerHelper("define", DefineHelper.INSTANCE)
        .registerHelpers(StringHelpers::class.java)
        .registerHelpers(ConditionalHelpers::class.java)
    if (forPath) {
        template.setStartDelimiter("{")
        template.setEndDelimiter("}")
    }
    return template
}

class NormalizedPropertyNameValueResolver(
    private val resolver: JavaBeanValueResolver = JavaBeanValueResolver()
) : ValueResolver by resolver {
    override fun resolve(context: Any, name: String): Any? {
        return resolver.resolve(context, name.lowerCamelize())
    }
    companion object {
        val INSTANCE = NormalizedPropertyNameValueResolver()
    }
}

class TemplateWrapper(private val template: Template) : Template by template {

    companion object {
        fun createContext(obj:Any) = Context.newBuilder(obj).resolver(
            NormalizedPropertyNameValueResolver.INSTANCE,
            FieldValueResolver.INSTANCE,
            MapValueResolver.INSTANCE,
            MethodValueResolver.INSTANCE
        ).build()
    }

    override fun apply(context: Any?): String = template.apply(createContext(context!!))
}

