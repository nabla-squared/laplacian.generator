package laplacian.gradle.task.generate

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

open class ModelSpec(
    private val project: Project
) {
    @Input
    val modelEntryResolvers = project.objects.listProperty(ModelEntryResolver::class.java)

    @InputFiles
    val modelFiles = project.files("model", "laplacian-module.yml", "laplacian-module.yaml")

    @Optional
    @Input
    val moduleNames = project.objects.listProperty(String::class.java)

    @Input
    val configurationName = project.objects
                           .property(String::class.java)
                           .value("model")

    val configuration = configurationName.map {
        project.configurations.getByName(it)
    }

    fun modelEntryResolver(resolver: ModelEntryResolver) {
        modelEntryResolvers.add(resolver)
    }

    fun files(vararg paths: Any) {
        modelFiles.from(*paths)
    }

    fun module(module: Dependency) {
        val name = module.name
        val version = module.version
        moduleNames.add("/${name}-${version}.jar")
    }

    fun applyTo(executionContext: ExecutionContext) {
        val configuration = configuration.get()
        moduleNames.get().forEach { path ->
            val archive = configuration.files.find{ it.absolutePath.endsWith(path) }
            val files = project.zipTree(archive).asFileTree
            modelFiles.from(files)
        }
        val yamlFiles = modelFiles.asFileTree.matching {
            it.include("**/*.yaml", "**/*.yml")
        }
        executionContext.modelFiles.addAll(yamlFiles)
        executionContext.modelEntryResolvers.add(ProjectEntryResolver())
        executionContext.modelEntryResolvers.addAll(modelEntryResolvers.get())
    }
}
