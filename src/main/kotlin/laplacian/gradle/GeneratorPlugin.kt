package laplacian.gradle

import laplacian.gradle.task.LaplacianGenerateTask
import laplacian.gradle.task.LaplacianGenerateExtension
import laplacian.gradle.task.generate.ModelSpec
import laplacian.gradle.task.generate.TemplateModuleSpec
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.lang.IllegalStateException

class GeneratorPlugin: Plugin<Project> {

    companion object {
        const val GENERATE_TASK_NAME = "laplacianGenerate"
        const val MODULE_TASK_NAME = "laplacianModule"
        const val MODULE_TEMPLATE = "laplacian:laplacian.template.module-base:1.0.0"
        const val CONFIGURATION_TEMPLATE = "template"
        const val CONFIGURATION_MODULE = "laplacianModuleTemplate"
    }

    override fun apply(project: Project) {
        setupConfigurationForTemplate(project)
        setupConfigurationForModule(project)
    }

    private fun setupConfigurationForTemplate(project: Project) {
        val templates = project.configurations.create(CONFIGURATION_TEMPLATE) {
            it.description = "The artifacts which contain laplacian generation template."
            it.isVisible = false
        }
        val extension = project.extensions.create(GENERATE_TASK_NAME, LaplacianGenerateExtension::class.java, project)
        project.tasks.register(GENERATE_TASK_NAME, LaplacianGenerateTask::class.java).configure { task ->
            templates.allDependencies.forEach { dependency ->
                extension.templateModule {
                    from(templates.name, dependency)
                }
            }
            extension.applyTo(task)
        }
    }

    private fun setupConfigurationForModule(project: Project) {
        val moduleTemplate = project.configurations.create(CONFIGURATION_MODULE) {
            it.description = "The artifacts that contain laplacian model files."
            it.isVisible = false
            project.dependencies.add(it.name, MODULE_TEMPLATE)
        }
        project.tasks.register(MODULE_TASK_NAME, LaplacianGenerateTask::class.java).configure { task ->
            task.modelSpec.set(
                ModelSpec(project).apply {
                    val moduleDef = project.files(
                        "laplacian-module.yml",
                        "laplacian-module.yaml"
                    )
                    if (moduleDef.files.all{!it.exists()}) throw IllegalStateException(
                        "A file which is named laplacian-module.ya?ml is needed in the root directory of this project. "
                    )
                    files(moduleDef)
                }
            )
            task.templateModuleSpecs.add(
                TemplateModuleSpec(project).apply {
                    moduleTemplate.allDependencies.forEach { dependency ->
                        from(moduleTemplate.name, dependency)
                    }
                }
            )
            task.prepare()
        }
    }
}
