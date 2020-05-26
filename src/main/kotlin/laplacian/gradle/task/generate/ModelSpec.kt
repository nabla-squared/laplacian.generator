package laplacian.gradle.task.generate

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import java.io.File

class ModelSpec(
    project: Project,
    val base: FileResourceSpec = FileResourceSpecBase(
        project, "model"
    )
) : FileResourceSpec by base {

    override fun toString() = base.toString()

    @Input
    val modelEntryResolvers = project.objects.listProperty(ModelEntryResolver::class.java)

    @InputFile
    @Optional
    val schemaFile = project.objects.fileProperty()

    fun modelSchema(path: Any) {
        schemaFile.set(project.file(path))
    }

    fun modelEntryResolver(resolver: ModelEntryResolver) {
        modelEntryResolvers.add(resolver)
    }

    fun applyTo(executionContext: ExecutionContext) {
        base.forEachFileSets { files ->
            val yamlFiles: List<File> = files.asFileTree.matching {
                it.include("**/*.yaml", "**/*.yml", "**/*.json", "**/*.js")
            }.filterNotNull()
            if (schemaFile.isPresent) executionContext.setModelSchema(schemaFile.asFile.get())
            executionContext.addModel(*yamlFiles.sorted().toTypedArray())
            executionContext.addModelEntryResolver(*modelEntryResolvers.get().toTypedArray())
        }
    }
}
