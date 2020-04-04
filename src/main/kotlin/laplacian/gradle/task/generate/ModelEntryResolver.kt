package laplacian.gradle.task.generate

import laplacian.util.RecordList
import java.io.Serializable

interface ModelEntryResolver: Serializable {
    fun resolves(key: String, model: Map<String, Any?>): Boolean
    fun resolve(key: String, model: Map<String, Any?>, context: ExecutionContext): Any?
}
