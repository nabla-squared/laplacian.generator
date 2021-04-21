package laplacian.handlebars

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Context
import laplacian.handlebars.helper.*
import laplacian.generate.util.*
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.util.UUID

fun Context.evalExpression(expression: String): Any? {
    if (expression.isNullOrBlank()) return null
    val key = UUID.randomUUID().toString()
    val template = HandlebarsUtil.buildTemplate("{{define '$key' $expression }}", Helpers.helpers())
    template.apply(this)
    return this.get(key)
}

class Helpers {
    companion object {

        val YAML = Yaml(DumperOptions().apply {
            isPrettyFlow = true
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            indent = 2
        })


        fun toYaml(obj: Any?, pad: String = ""): String =
            YAML.dump(obj).trim().also {
                return if (pad.isEmpty())
                    it
                else
                    it.replace(END_OF_LINE_EXCLUDING_EOF, "\n$pad")
            }

        private val END_OF_LINE_EXCLUDING_EOF = """\n(?!$)""".toRegex()

        fun literalize(value: Any?): String = when (value) {
            null -> "null"
            is String -> "\"$value\""
            is List<*> -> "listOf(${value.joinToString(", ") { literalize(it) }})"
            is Map<*, *> -> """mapOf(${value.map { "${literalize(it.key)} to ${literalize(it.value)}" }.joinToString(", ")})"""
            else -> value.toString()
        }

        fun helpers(): Map<String, Helper<*>> = HELPERS
        private val HELPERS = mapOf(
            "lower-camel" to StringHelper { t, _ -> t.lowerCamelize() },
            "upper-camel" to StringHelper { t, _ -> t.upperCamelize() },
            "capitalize-first" to StringHelper { t, _ -> t.capitalizeFirst() },
            "hyphen" to StringHelper { t, _ -> t.lowerHyphenize() },
            "lower-underscore" to StringHelper { t, _ -> t.lowerUnderscorize() },
            "lower-snake" to StringHelper { t, _ -> t.lowerUnderscorize() },
            "upper-underscore" to StringHelper { t, _ -> t.upperUnderscorize() },
            "upper-snake" to StringHelper { t, _ -> t.upperUnderscorize() },
            "dot-delimited" to StringHelper { t, _ -> t.dotDelimited() },
            "space-delimited" to StringHelper { t, _ -> t.spaceDelimited() },
            "path" to StringHelper { t, _ -> t.pathify() },
            "plural" to StringHelper { t, _ -> t.pluralize() },
            "shift" to StringHelper { t, opts ->
                val width = opts.hash.getOrElse("width") { opts.params[0] } as Int
                t.shift(width)
            },
            "trim" to StringHelper { t, opts ->
                val chars = opts.hash.getOrElse("chars") { opts.params.getOrNull(0) }?.toString()
                if (chars == null)
                    t.trim()
                else
                    t.trim { it in chars || it.isWhitespace() }
            },
            "printf" to StringHelper { t, opts -> t.format(*opts.params) },
            "replace" to StringHelper { t, opts ->
                val params = opts.params
                if (params.size < 2) throw IllegalArgumentException(
                    "Replace helper requires 3 parameters. Usage: {{replace target pattern replacement}}"
                )
                if (params[0] == null || params[1] == null) throw IllegalArgumentException(
                    "Replace helper's parameters must not be null: {{replace '$t' '${params[0]}' '${params[1]}'}}"
                )
                t.replace(
                    params[0].toString().toRegex(),
                    params[1].toString()
                )
            },
            "line-continuation" to StringHelper { t, _ ->
                t.replace("""\n""".toRegex(), "\\\\\n")
            },
            "java-property-value" to StringHelper { t, _ ->
                t.trim()
                    .replace("""([:=\\])""".toRegex(), "\\\\$1")
                    .replace("""\n""".toRegex(), "\\\\\n")
            },
            "dquote" to StringHelper { t, _ -> t.dquote() },
            "starts-with" to StringHelper { t, opts ->
                val prefix = opts.params.first().toString()
                if (t.startsWith(prefix)) t.substring(prefix.length) else ""
            },
            "ends-with" to StringHelper { t, opts ->
                val suffix = opts.params.first().toString()
                if (t.endsWith(suffix)) t.substring(0, t.length - suffix.length) else ""
            },
            "not-null" to BoolHelper { t, _ -> (t != null) },
            "is-null" to BoolHelper { t, _ -> (t == null) },
            "contains" to StringHelper { t, opts ->
                val substring = opts.params.first().toString()
                if (t.contains(substring)) substring else ""
            },
            "yaml" to StringifyHelper<Any?> { t, opts ->
                toYaml(t, opts.params.getOrNull(0)?.toString() ?: "")
            },
            "json" to StringifyHelper<Any?> { obj, opts ->
                val padding = opts.params.getOrNull(0)?.toString() ?: ""
                val mapper = ObjectMapper()
                var isHead = true
                if (padding.isEmpty()) mapper.writeValueAsString(obj)
                else mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj).split("\n").joinToString("\n") {
                    if (isHead) {
                        isHead = false
                        it
                    } else {
                        padding + it
                    }
                }
            },
            "eval-template" to StringHelper { t, opts -> HandlebarsUtil.buildTemplate(t, helpers()).apply(opts.context) },
            "literal" to StringifyHelper<Any?> { t, _ -> literalize(t) },
            "first" to ListHelper { l, _ -> l.first() },
            "any" to ListHelper { l, opts ->
                l.any {
                    val expr = opts.params[0].toString()
                    val context = opts.context
                    val value = context.combine("@it", it).evalExpression(expr)
                    !opts.isFalsy(value)
                }
            },
            "find" to ListHelper { l, opts ->
                l.find {
                    val expr = opts.params[0].toString()
                    val context = opts.context
                    val value = context.combine("@it", it).evalExpression(expr)
                    !opts.isFalsy(value)
                }
            },
            "map" to ListHelper { l, opts ->
                l.map {
                    val expr = opts.params[0].toString()
                    val context = opts.context
                    context.combine("@it", it!!).evalExpression(expr)
                }
            },
            "filter" to ListHelper { l, opts ->
                l.filter {
                    val expr = opts.params[0].toString()
                    val context = opts.context
                    val value = context.combine("@it", it).evalExpression(expr)
                    !opts.isFalsy(value)
                }
            },
            "sort" to ListHelper { l: List<Any?>, opts ->
                val nullValue = ""
                if (opts.params.isEmpty()) {
                    l.sortedBy { it?.toString() ?: nullValue }
                } else {
                    val expr = opts.params[0].toString()
                    val context = opts.context
                    l.sortedBy {
                        context.combine("@it", it)
                            .evalExpression(expr)?.toString() ?: nullValue
                    }
                }
            },
            "unique" to ListHelper { l, _ -> l.distinct() },
            "block-join" to JoinHelper.INSTANCE,
            "if" to IfHelper.INSTANCE,
            "case" to CaseHelper.INSTANCE,
            "concat" to ConcatHelper.INSTANCE,
            "each" to EachHelper.INSTANCE,
            "lookup" to LookupHelper.INSTANCE,
            "eval-expression" to EvalExpressionHelper.INSTANCE,
            "contains-key" to ContainsKeyHelper.INSTANCE,
            "list-of" to ListOfHelper.INSTANCE,
            "define" to DefineHelper.INSTANCE,
            FakeHelper.NAME to FakeHelper.INSTANCE,
            // TODO move to dedicated plugins
            "table-name" to StringHelper { t, _ -> "t_" + t.lowerUnderscorize() },
            "column-name" to StringHelper { t, _ -> t.lowerUnderscorize() },
        )
    }
}


