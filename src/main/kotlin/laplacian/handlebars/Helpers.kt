package laplacian.handlebars

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.Context
import laplacian.handlebars.helper.*
import laplacian.util.*
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.util.UUID

fun Context.evalExpression(expression: String): Any? {
    if (expression.isNullOrBlank()) return null
    val key = UUID.randomUUID().toString()
    val template = "{{define '$key' $expression }}".handlebars()
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

        fun literalize(value: Any?): String = when(value) {
            null -> "null"
            is String -> "\"$value\""
            is List<*> -> "listOf(${value.joinToString(", ") { literalize(it) }})"
            is Map<*,*> -> """mapOf(${value.map{ "${literalize(it.key)} to ${literalize(it.value)}" }.joinToString(", ")})"""
            else -> value.toString()
        }

        fun registerTo(handlebars: Handlebars) {
            handlebars
            .registerHelper("lower-camel", StringHelper{ t, _ -> t.lowerCamelize()})
            .registerHelper("upper-camel", StringHelper{ t, _ -> t.upperCamelize()})
            .registerHelper("capitalize-first", StringHelper{ t, _ -> t.capitalizeFirst()})
            .registerHelper("hyphen", StringHelper{ t, _ -> t.lowerHyphenize()})
            .registerHelper("lower-underscore", StringHelper{ t, _ -> t.lowerUnderscorize()})
            .registerHelper("lower-snake", StringHelper{ t, _ -> t.lowerUnderscorize()})
            .registerHelper("upper-underscore", StringHelper{ t, _ -> t.upperUnderscorize()})
            .registerHelper("upper-snake", StringHelper{ t, _ -> t.upperUnderscorize()})
            .registerHelper("dot-delimited", StringHelper{ t, _ -> t.dotDelimited()})
            .registerHelper("space-delimted", StringHelper{ t, _ -> t.spaceDelimited()})
            .registerHelper("path", StringHelper{t, _ -> t.pathify()})
            .registerHelper("plural", StringHelper{ t, _ -> t.pluralize()})
            .registerHelper("shift", StringHelper{ t, opts ->
                val width = opts.hash.getOrElse("width"){opts.params[0]} as Int
                t.shift(width)
            })
            .registerHelper("trim", StringHelper{ t, opts ->
                val chars = opts.hash.getOrElse("chars"){opts.params.getOrNull(0)}?.toString()
                if (chars == null)
                    t.trim()
                else
                    t.trim { it in chars || it.isWhitespace() }
            })
            .registerHelper("printf", StringHelper{ t, opts -> t.format(*opts.params) })
            .registerHelper("replace", StringHelper{ t, opts ->
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
            })
            .registerHelper("dquote", StringHelper{ t, _ -> t.dquote()})
            .registerHelper("starts-with", StringHelper{t, opts ->
                val prefix = opts.params.first().toString()
                if (t.startsWith(prefix)) t.substring(prefix.length) else ""
            })
            .registerHelper("ends-with", StringHelper{t, opts ->
                val suffix = opts.params.first().toString()
                if (t.endsWith(suffix)) t.substring(0, t.length - suffix.length) else ""
            })
            .registerHelper("yaml", StringifyHelper<Any?>{ t, opts -> toYaml(t, opts.params.getOrNull(0)?.toString() ?: "") })
            .registerHelper("json", StringifyHelper<Any?>{ obj, opts ->
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
            })
            .registerHelper("eval-template", StringHelper{ t, opts -> t.handlebars().apply(opts.context) })
            .registerHelper("literal", StringifyHelper<Any?>{ t, _ -> literalize(t) })
            .registerHelper("first", ListHelper{ l, _ -> l.first() })
            .registerHelper("any", ListHelper{ l, opts -> l.any {
                val expr = opts.params[0].toString()
                val context = opts.context
                val value = context.combine("@it", it!!).evalExpression(expr)
                !opts.isFalsy(value)
            }})
            .registerHelper("map", ListHelper{ l, opts -> l.map {
                val expr = opts.params[0].toString()
                val context = opts.context
                context.combine("@it", it!!).evalExpression(expr)
            }})
            .registerHelper("filter", ListHelper{ l, opts -> l.filter {
                val expr = opts.params[0].toString()
                val context = opts.context
                val value = context.combine("@it", it!!).evalExpression(expr)
                !opts.isFalsy(value)
            }})
            .registerHelper("sort", ListHelper{ l: List<Any?>, opts ->
                val nullValue = ""
                if (opts.params.isEmpty()) {
                    l.sortedBy{ it?.toString() ?: nullValue }
                }
                else {
                    val expr = opts.params[0].toString()
                    val context = opts.context
                    l.sortedBy {
                        context.combine("@it", it!!)
                               .evalExpression(expr)?.toString() ?: nullValue
                    }
                }
            })
            .registerHelper("unique", ListHelper{ l, _ -> l.distinct() })
            .registerHelper("block-join", JoinHelper.INSTANCE)
            .registerHelper("if" , IfHelper.INSTANCE)
            .registerHelper("case" , CaseHelper.INSTANCE)
            .registerHelper("concat" , ConcatHelper.INSTANCE)
            .registerHelper("each", EachHelper.INSTANCE)
            .registerHelper("lookup", LookupHelper.INSTANCE)
            .registerHelper("eval-expression", EvalExpressionHelper.INSTANCE)
            .registerHelper("contains-key", ContainsKeyHelper.INSTANCE)
            .registerHelper("list-of", ListOfHelper.INSTANCE)
            .registerHelper("define", DefineHelper.INSTANCE)
        }
    }
}


