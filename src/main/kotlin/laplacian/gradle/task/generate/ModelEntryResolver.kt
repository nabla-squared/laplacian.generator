package laplacian.gradle.task.generate

import laplacian.util.RecordList
import java.io.Serializable

interface ModelEntryResolver: Serializable {
    fun resolves(key: String, model: Map<String, RecordList>): Boolean
    fun resolve(key: String, model: Map<String, RecordList>, context: ExecutionContext): Any?
}
