package laplacian.handlebars.resolver

import com.github.jknack.handlebars.ValueResolver
import com.github.jknack.handlebars.context.JavaBeanValueResolver
import laplacian.generate.util.*

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
