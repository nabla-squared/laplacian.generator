package laplacian.generate

import com.github.jknack.handlebars.ValueResolver
import com.github.jknack.handlebars.context.MapValueResolver
import laplacian.generate.util.RecordList

class ModelEntryResolverBridge(
    private val resolver: ModelEntryResolver,
    private val context: ExecutionContext
): ValueResolver by MapValueResolver.INSTANCE {
    override fun resolve(model: Any, name: String): Any? {
        if (context.baseModel.model() !== model) {
            return ValueResolver.UNRESOLVED
        }
        val m = model as Map<String, RecordList>
        if (resolver.resolves(name, m)) {
            return resolver.resolve(name, m, context)
        }
        return ValueResolver.UNRESOLVED
    }
}
