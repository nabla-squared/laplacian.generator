package laplacian.gradle.task.generate

import laplacian.util.*

class Project(
    model: Map<String, Any?>
): Module(model) {

    val namespace: String
        get() = if (containsKey("namespace"))
                getString("namespace")
            else
                moduleSignature().map(String::lowerUnderscorize).joinToString(".")

    val models: List<Module>
        get() = model
               .getList<Map<String, Any?>>("models", emptyList())
               .map{ Module(it + ("type" to "model")) }

    val templates: List<Module>
        get() = model
               .getList<Map<String, Any?>>("templates", emptyList())
               .map{ Module(it + ("type" to "template")) }
}
