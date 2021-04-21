package laplacian.generate

import org.pf4j.ExtensionPoint

interface ModelEntryResolver: ExtensionPoint {
    fun resolves(key: String, model: Map<String, Any?>): Boolean
    fun resolve(key: String, model: Map<String, Any?>, context: ExecutionContext): Any?
}
