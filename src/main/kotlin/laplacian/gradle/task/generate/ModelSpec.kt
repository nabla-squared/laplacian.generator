package laplacian.gradle.task.generate

import org.gradle.api.Project
import org.gradle.api.tasks.Input

class ModelSpec(
    project: Project,
    val base: FileResourceSpec = FileResourceSpecBase(
        project,
        arrayOf("model", "laplacian-module.yml", "laplacian-module.yaml"),
        "model"
    )
) : FileResourceSpec by base {

    override fun toString() = base.toString()

    @Input
    val modelEntryResolvers = project.objects.listProperty(ModelEntryResolver::class.java)

    fun modelEntryResolver(resolver: ModelEntryResolver) {
        modelEntryResolvers.add(resolver)
    }

    fun applyTo(executionContext: ExecutionContext) {
        base.forEachFileSets { files ->
            val yamlFiles = files.asFileTree.matching {
                it.include("**/*.yaml", "**/*.yml")
            }
            executionContext.modelFiles.addAll(yamlFiles)
            executionContext.modelEntryResolvers.add(ProjectEntryResolver())
            executionContext.modelEntryResolvers.addAll(modelEntryResolvers.get())
        }
    }
}
