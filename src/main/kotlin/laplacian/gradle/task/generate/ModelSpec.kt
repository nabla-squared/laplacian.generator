package laplacian.gradle.task.generate

import laplacian.DefaultModelLoader
import laplacian.ModelLoader
import laplacian.util.TemplateWrapper
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

open class ModelSpec(
    private val project: Project
) {
    val modelLoader = project.objects
        .property(ModelLoader::class.java)
        .value(DefaultModelLoader())

    @Optional
    @InputDirectory
    val modelDir = project.objects.directoryProperty()

    @Optional
    @InputFiles
    val modelFiles = project.files()


    fun loader(loader: ModelLoader) {
        modelLoader.set(loader)
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
        val model = modelLoader.get().load(yamlFiles)
        executionContext.baseModel = TemplateWrapper.createContext(model)
    }
}
