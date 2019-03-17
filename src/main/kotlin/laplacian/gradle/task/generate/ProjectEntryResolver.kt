package laplacian.gradle.task.generate

import laplacian.util.RecordList
import java.lang.IllegalStateException

class ProjectEntryResolver: ModelEntryResolver {

    override fun resolves(key: String, model: Map<String, RecordList>): Boolean {
        return key == "project"
    }

    override fun resolve(key: String, model: Map<String, RecordList>, context: ExecutionContext): Any? {
        val project = model.getOrElse(key) {
            throw IllegalStateException(
                "A project definition entry is required in laplacian-module.yml."
            )
        } as Map<String, Any?>
        return Project(project)
    }

}
