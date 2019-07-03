package laplacian.gradle

import laplacian.gradle.task.LaplacianGenerateTask
import laplacian.gradle.task.LaplacianGenerateExtension
import laplacian.gradle.task.LaplacianModuleTask
import laplacian.gradle.task.generate.ModelSpec
import laplacian.gradle.task.generate.TemplateSpec
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException

class GeneratorPlugin: Plugin<Project> {

    companion object {
        const val GENERATE_TASK_NAME = "laplacianGenerate"
        const val MODULE_TASK_NAME = "laplacianModule"
        const val MODULE_TEMPLATE = "laplacian:laplacian.template.module-base:1.0.0"
        const val CONFIGURATION_TEMPLATE = "template"
        const val CONFIGURATION_MODEL = "model"
        const val CONFIGURATION_MODULE = "laplacianModuleTemplate"
        val LOG = LoggerFactory.getLogger(GeneratorPlugin::class.java)
    }

    override fun apply(project: Project) {
        registerGeneratorTask(project)
        setupModelConfiguration(project)
        setupTemplateConfiguration(project)
        registerModuleTask(project)
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
        val extension = project.extensions.getByType(LaplacianGenerateExtension::class.java)
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

    private fun registerModuleTask(project: Project) {
        val moduleTemplate = project.configurations.create(CONFIGURATION_MODULE) {
            it.description = "The artifacts that contain laplacian model files."
            it.isVisible = false
            project.dependencies.add(it.name, MODULE_TEMPLATE)
        }
        project.tasks.register(MODULE_TASK_NAME, LaplacianModuleTask::class.java).configure { task ->
            task.modelSpec.set(
                ModelSpec(project).apply {
                    val moduleDef = project.files(
                        "laplacian-module.yml",
                        "laplacian-module.yaml"
                    )
                    if (moduleDef.files.all{!it.exists()}) throw IllegalStateException(
                        "A file which is named laplacian-module.ya?ml is needed in the root directory of this project. "
                    )
                    from(moduleDef)
                }
            )
            task.templateSpec.set(
                TemplateSpec(project).apply {
                    files.setFrom()
                    configurationName.set(moduleTemplate.name)
                    moduleTemplate.allDependencies.forEach { dependency ->
                        module(dependency)
                    }
                }
            )
            task.prepare()
        }
    }
}
