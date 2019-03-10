package laplacian.gradle.task.generate

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

open class ModelSpec(
    private val project: Project
) {
    @Input
    val modelEntryResolvers = project.objects.listProperty(ModelEntryResolver::class.java)

    @Optional
    @InputDirectory
    val modelDir = project.objects.directoryProperty()

    @Optional
    @InputFiles
    val modelFiles = project.files()

    fun modelEntryResolver(resolver: ModelEntryResolver) {
        modelEntryResolvers.add(resolver)
    }

    fun files(vararg paths: Any) {
        modelFiles.setFrom(*paths)
    }

    fun dir(path: String) {
        modelDir.set(project.layout.projectDirectory.dir(path))
    }

    fun applyTo(executionContext: ExecutionContext) {
        val files = if (modelDir.isPresent) modelDir.asFileTree
                    else modelFiles.asFileTree
        val yamlFiles = files.matching {
            it.include("**/*.yaml", "**/*.yml")
        }
        executionContext.modelFiles.addAll(yamlFiles)
        executionContext.modelEntryResolvers.addAll(modelEntryResolvers.get())
    }
}
