package laplacian.gradle.task.generate

import laplacian.util.*

class Project(
    model: Map<String, Any?>
): Module(model) {

    val namespace: String
        get() = if (containsKey("namespace"))
                    getString("namespace")
                else
                    listOfNotNull(group, name, subname)
                   .map{ it.toString().lowerUnderscorize() }
                   .joinToString(".")

    val plugins: List<Module>
        get() = model
               .getList<Map<String, Any?>>("plugins", emptyList())
               .map{ Module(it + ("type" to "plugin")) }

    val pluginsExcludingSelf: List<Module>
        get() = plugins
               .filter{ it.moduleId != this.moduleId }

    val models: List<Module>
        get() = model
               .getList<Map<String, Any?>>("models", emptyList())
               .map{ Module(it + ("type" to "model")) }

    val templates: List<Module>
        get() = model
               .getList<Map<String, Any?>>("templates", emptyList())
               .map{ Module(it + ("type" to "template")) }
}
