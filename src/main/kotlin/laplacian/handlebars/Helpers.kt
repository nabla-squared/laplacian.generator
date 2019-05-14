package laplacian.handlebars

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import laplacian.handlebars.helper.DefineHelper
import laplacian.handlebars.helper.JoinHelper
import laplacian.handlebars.helper.ListHelper
import laplacian.handlebars.helper.StringHelper
import laplacian.util.*


class Helpers {
    companion object {
        val IDENTIFIER_TOKEN = """[-_a-zA-Z][-_a-zA-Z0-9$]*""".toRegex()
        val IDENTIFIER_TOKEN_WIDE = """[-_a-zA-Z][-./_a-zA-Z0-9$]*""".toRegex()

        fun identifierHelper(wide: Boolean = false, fn: (str: String, opts: Options) -> String): Helper<Any> = StringHelper() { str, opts ->
            str.replace(if (wide) IDENTIFIER_TOKEN_WIDE else IDENTIFIER_TOKEN) { fn(it.value, opts) }
        }

        fun registerTo(handlebars: Handlebars) {
            handlebars
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
            .registerHelper("concat", ListHelper{ l, opts -> l + ListHelper.asList(opts.params[0]) })
            .registerHelper("map", ListHelper{ l, opts -> l.map{ i -> TemplateWrapper.createContext(i!!)[opts.params[0].toString()] }})
            .registerHelper("unique", ListHelper{ l, _ -> l.distinct() })
            .registerHelper("block-join", JoinHelper.INSTANCE)
            .registerHelper("define", DefineHelper.INSTANCE)
        }
    }
}
