package laplacian.handlebars

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
            .registerHelper("hyphen", StringHelper{ t, _ -> t.lowerHyphenize()})
            .registerHelper("lower-underscore", StringHelper{ t, _ -> t.lowerUnderscorize()})
            .registerHelper("lower-snake", StringHelper{ t, _ -> t.lowerUnderscorize()})
            .registerHelper("upper-underscore", StringHelper{ t, _ -> t.upperUnderscorize()})
            .registerHelper("upper-snake", StringHelper{ t, _ -> t.upperUnderscorize()})
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
            .registerHelper("dquote", StringHelper{ t, _ -> t.dquote()})
            .registerHelper("yaml", StringifyHelper<Any?>{ t, opts -> toYaml(t, opts.params.getOrNull(0)?.toString() ?: "") })
            .registerHelper("eval-template", StringHelper{ t, opts -> t.handlebars().apply(opts.context) })
            .registerHelper("literal", StringifyHelper<Any?>{ t, _ -> literalize(t) })
            .registerHelper("map", ListHelper{ l, opts -> l.map {
                val expr = opts.params[0].toString()
                TemplateWrapper.createContext(it!!).evalExpression(expr)
            }})
            .registerHelper("filter", ListHelper{ l, opts -> l.filter {
                val expr = opts.params[0].toString()
                val value = TemplateWrapper.createContext(it!!).evalExpression(expr)
                !opts.isFalsy(value)
            }})
            .registerHelper("unique", ListHelper{ l, _ -> l.distinct() })
            .registerHelper("block-join", JoinHelper.INSTANCE)
            .registerHelper("if" , IfHelper.INSTANCE)
            .registerHelper("case" , CaseHelper.INSTANCE)
            .registerHelper("concat" , ConcatHelper.INSTANCE)
            .registerHelper("each", EachHelper.INSTANCE)
            .registerHelper("lookup", LookupHelper.INSTANCE)
            .registerHelper("eval-expression", EvalExpressionHelper.INSTANCE)
            .registerHelper("contains-key", ContainsKeyHelper.INSTANCE)
            .registerHelper("define", DefineHelper.INSTANCE)
        }
    }
}


