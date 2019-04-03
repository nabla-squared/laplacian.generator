package laplacian.gradle.task.generate
import laplacian.util.*

open class Module(
    val model: Map<String, Any?>
): Map<String, Any?> by model {

    fun required(key: String): String {
        return getOrElse(key) {
            throw IllegalStateException("$key is required in the model definition.")
        } as String
    }

    val name: String
        get() = required("name")

    val type: String
        get() = required("type")

    val group: String
        get() = required("group")

    val subname: String?
        get() = model["subname"] as String?

    val version: String
        get() = required("version")

    val isTemplate: Boolean
        get() = (type == "template")

    val isModel: Boolean
        get() = (type == "model")

    val isPlugin: Boolean
        get() = (type == "plugin")

    val isProject: Boolean
        get() = (type == "project")

    val description: String
        get() = model.getOrElse("description") {
            "${group.toUpperCase()}/${name.toUpperCase()} - a laplacian $type project"
        }.toString()

    fun moduleSignature() = listOfNotNull(group, type, name, subname).map{ it.toString() }

    val moduleId: String
        get() = moduleSignature().map(String::lowerHyphenize).joinToString(".")

    val artifactId: String
        get() = "$group:$moduleId:${required("version")}"
}
