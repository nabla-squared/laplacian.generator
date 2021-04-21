package laplacian.handlebars

import com.github.jknack.handlebars.EscapingStrategy
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.helper.ConditionalHelpers
import com.github.jknack.handlebars.helper.StringHelpers
import com.github.jknack.handlebars.io.FileTemplateLoader
import laplacian.handlebars.helper.DefineHelper
import laplacian.handlebars.helper.EachHelper
import laplacian.handlebars.helper.IfHelper
import java.io.File

class HandlebarsUtil {
    companion object {
        fun buildTemplateForPath(
            template: String,
            helpers: Map<String, Helper<*>> = emptyMap(),
            basePath: File? = null,
        ) = buildTemplate(template, helpers, basePath, forPath = true)

        fun buildTemplate(
            template: String,
            helpers: Map<String, Helper<*>> = emptyMap(),
            basePath: File? = null,
            forPath: Boolean = false,
        ): Template {
            val handlebars = Handlebars()
                .with(EscapingStrategy.NOOP)
                .registerHelpers(StringHelpers::class.java)
                .registerHelpers(ConditionalHelpers::class.java)
                .registerHelper("if", IfHelper.INSTANCE)
                .registerHelper("define", DefineHelper.INSTANCE)
                .registerHelper("each", EachHelper.INSTANCE)
                .also{ helpers.forEach{ (name, helper) -> it.registerHelper(name, helper) }}
            if (forPath) {
                handlebars.setStartDelimiter("{")
                handlebars.setEndDelimiter("}")
            }
            if (basePath != null) {
                handlebars.with(FileTemplateLoader(basePath))
            }
            return TemplateWrapper(handlebars.compileInline(template))
        }
    }
}
