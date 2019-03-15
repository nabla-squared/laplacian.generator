package laplacian.gradle.task.generate

import laplacian.gradle.filter.HandlebarsFilter
import laplacian.gradle.task.LaplacianGenerateTask.Companion.REPLACED_FILE_NAME
import laplacian.gradle.task.LaplacianGenerateTask.Companion.TEMPLATE_GLOB
import laplacian.gradle.task.LaplacianGenerateTask.Companion.TEMPLATE_PATTERN
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory

open class TemplateModuleSpec(
    private val project: Project
) {
    @Optional
    @OutputDirectory
    val into = project.objects
              .property(String::class.java)
              .value("./")

    @Input
    val from = project.objects
              .property(String::class.java)

    @Input
    val configurationName = project.objects
                           .property(String::class.java)
                           .value("template")

    val configuration = configurationName.map {
        project.configurations.getByName(it)
    }

    @InputFile
    val fromModule = from.map { name ->
        val matchesPath = name.contains("/")
        configuration.get().files.find { file ->
            if (matchesPath)
                file.absolutePath.endsWith(name)
            else
                file.name!!.contains(name)
        } ?: throw IllegalStateException(
            "Unknown dependency: $name"
        )
    }

    fun into(path: String) {
        into.set(path)
    }

    fun from(moduleName: String) {
        from.set(moduleName)
    }

    fun from(configuration: String, moduleName: String) {
        configurationName.set(configuration)
        from(moduleName)
    }

    fun from(module: Dependency) {
        val name = module.name
        val version = module.version
        from.set("/${name}-${version}.jar")
    }

    fun from(configuration: String, module: Dependency) {
        configurationName.set(configuration)
        from(module)
    }

    fun applyTo(copySpec: CopySpec, filterOpts: Map<String, Any>) {
        val fromFiles = project.zipTree(
            fromModule.get()
        )
        copySpec.into(into.get())
        copySpec.from(fromFiles) {
            it.exclude(*TEMPLATE_GLOB, "META-INF/**")
        }
        copySpec.from(fromFiles) {
            it.include(*TEMPLATE_GLOB)
            it.exclude("META-INF/**")
            it.filter(filterOpts, HandlebarsFilter::class.java)
            it.rename(TEMPLATE_PATTERN, REPLACED_FILE_NAME)
        }
    }
}
