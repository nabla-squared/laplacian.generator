package laplacian.gradle.task.generate

import java.lang.IllegalStateException

class Project(
    model: Map<String, Any?>
): Map<String, Any?> by model {
    private fun required(key: String): String {
        return getOrElse(key) {
            throw IllegalStateException("$key is required in a project definition.")
        } as String
    }
    val namespace: String
        get() = listOfNotNull(
            required("group"), required("name"), get("subname")
        ).joinToString(".")
}
