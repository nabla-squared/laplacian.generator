package laplacian.gradle

import laplacian.gradle.task.LaplacianGenerateTask
import laplacian.gradle.task.LaplacianGenerateExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class GeneratorPlugin: Plugin<Project> {

    companion object {
        const val GENERATE_TASK_NAME = "laplacianGenerate"
        const val CONFIGURATION_TEMPLATE = "template"
        const val CONFIGURATION_MODEL = "model"
        val LOG = LoggerFactory.getLogger(GeneratorPlugin::class.java)
    }

    override fun apply(project: Project) {
        registerGeneratorTask(project)
        setupModelConfiguration(project)
        setupTemplateConfiguration(project)
    }

    private fun registerGeneratorTask(project: Project) {
        project.extensions.create(
            GENERATE_TASK_NAME, LaplacianGenerateExtension::class.java, project
        )
        project.tasks.register(
            GENERATE_TASK_NAME, LaplacianGenerateTask::class.java
        ) {
            it.group = "laplacian generator"
            it.description = "generates files from template files and models"
        }
    }

    private fun setupModelConfiguration(project: Project) {
        val configuration = project.configurations.create(CONFIGURATION_MODEL) {
            it.description = "The artifacts which contain laplacian model definitions."
            it.isVisible = false
        }
        val extension = project.extensions.getByType(
            LaplacianGenerateExtension::class.java
        )
        project.tasks.named(GENERATE_TASK_NAME, LaplacianGenerateTask::class.java).configure {
            configuration.allDependencies.forEach { dependency ->
                extension.model {
                    module(dependency)
                }
            }
        }
    }

    private fun setupTemplateConfiguration(project: Project) {
        val configuration = project.configurations.create(CONFIGURATION_TEMPLATE) {
            it.description = "The artifacts which contain laplacian generation templates."
            it.isVisible = false
        }
        val extension = project.extensions.getByType(LaplacianGenerateExtension::class.java)
        project.tasks.named(GENERATE_TASK_NAME, LaplacianGenerateTask::class.java).configure { task ->
            configuration.allDependencies.forEach { dependency ->
                extension.template {
                    module(dependency)
                }
            }
            extension.applyTo(task)
        }
    }
}
