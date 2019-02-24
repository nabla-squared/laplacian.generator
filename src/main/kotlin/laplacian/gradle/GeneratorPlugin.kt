package laplacian.gradle

import laplacian.gradle.task.LaplacianGenerateTask
import laplacian.gradle.task.LaplacianGenerateExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class GeneratorPlugin: Plugin<Project> {

    companion object {
        const val TASK_NAME = "laplacianGenerate"
    }

    override fun apply(project: Project) {
        project.configurations.create("model") {
            it.description = "The artifacts that contain laplacian model files."
            it.isVisible = false
        }
        project.configurations.create("template") {
            it.description = "The artifacts which contain laplacian generation template."
            it.isVisible = false
        }
        val extension = project.extensions.create(TASK_NAME, LaplacianGenerateExtension::class.java, project)
        project.tasks.register(TASK_NAME, LaplacianGenerateTask::class.java).configure {
            extension.applyTo(it)
        }
    }
}
