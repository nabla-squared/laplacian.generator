package laplacian.gradle.task.generate

import laplacian.util.*

class Project(
    model: Map<String, Any?>
): Map<String, Any?> by model {
    private fun required(key: String): String {
        return getOrElse(key) {
            throw IllegalStateException("$key is required in a project definition.")
        } as String
    }
    val namespace: String
        get() = if (containsKey("namespace"))
                getString("namespace")
            else
                listOfNotNull(
                    required("group"), required("type"), required("name"), get("subname")
                ).map{ it.toString().lowerUnderscorize() }.joinToString(".")
}
