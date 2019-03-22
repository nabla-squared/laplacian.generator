package laplacian.gradle.task.generate
import laplacian.util.*

open class Module(
    val model: Map<String, Any?>
): Map<String, Any?> by model {
    private fun required(key: String): String {
        return getOrElse(key) {
            throw IllegalStateException("$key is required in the model definition.")
        } as String
    }

    fun moduleSignature() = listOfNotNull(
        required("group"), required("type"), required("name"), get("subname")
    ).map{ it.toString() }

    val moduleId: String
        get() = moduleSignature().map(String::lowerHyphenize).joinToString(".")

    val artifactId: String
        get() = "${required("group")}:$moduleId:${required("version")}"
}
