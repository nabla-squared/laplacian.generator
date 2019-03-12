package laplacian.gradle.task.generate

import laplacian.util.RecordList

interface ModelEntryResolver {
    fun resolves(key: String, model: Map<String, RecordList>): Boolean
    fun resolve(key: String, model: Map<String, RecordList>, context: ExecutionContext): Any?
}
