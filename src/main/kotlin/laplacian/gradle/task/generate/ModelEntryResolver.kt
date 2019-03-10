package laplacian.gradle.task.generate

import laplacian.util.RecordList

class ModelEntryResolver(
    val resolves: (key: String, model: Map<String, RecordList>) -> Boolean,
    val resolve: (key: String, model: Map<String, RecordList>, context: ExecutionContext) -> Any?
)
