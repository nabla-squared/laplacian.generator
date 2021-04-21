package laplacian.handlebars

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.ValueResolver
import com.github.jknack.handlebars.context.FieldValueResolver
import com.github.jknack.handlebars.context.MapValueResolver
import com.github.jknack.handlebars.context.MethodValueResolver
import laplacian.handlebars.resolver.NormalizedPropertyNameValueResolver

class TemplateWrapper(private val template: Template) : Template by template {
    companion object {
        fun createContext(
            model: Any,
            vararg resolvers: ValueResolver
        ) = Context.newBuilder(model).resolver(
            *resolvers,
            NormalizedPropertyNameValueResolver.INSTANCE,
            FieldValueResolver.INSTANCE,
            MapValueResolver.INSTANCE,
            MethodValueResolver.INSTANCE
        ).build()
    }
    override fun apply(context: Any?): String = template.apply(createContext(context!!))
}
