package laplacian.gradle.task.generate

import laplacian.DefaultModelLoader
import laplacian.ModelLoader
import laplacian.util.TemplateWrapper
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory

open class ModelSpec(
    private val project: Project
) {
    val modelLoader = project.objects
        .property(ModelLoader::class.java)
        .value(DefaultModelLoader())

    @InputDirectory
    val modelDir = project.objects.directoryProperty()

    fun loader(loader: ModelLoader) {
        modelLoader.set(loader)
    }

    fun dir(path: String) {
        modelDir.set(project.layout.projectDirectory.dir(path))
    }

    fun applyTo(executionContext: ExecutionContext) {
        val files = modelDir.asFileTree.matching {
            it.include("**/*.yaml", "**/*.yml")
        }
        val model = modelLoader.get().load(files)
        executionContext.baseModel = TemplateWrapper.createContext(model)
    }
}
