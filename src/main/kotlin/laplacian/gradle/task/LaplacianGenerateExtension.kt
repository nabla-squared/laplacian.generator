package laplacian.gradle.task

import laplacian.gradle.task.generate.ModelSpec
import laplacian.gradle.task.generate.TemplateDirSpec
import laplacian.gradle.task.generate.TemplateFileSpec
import laplacian.gradle.task.generate.TemplateModuleSpec
import org.gradle.api.Project
import org.gradle.api.tasks.*

open class LaplacianGenerateExtension constructor(
    private val project: Project
) {
    @Nested
    val modelSpec = project.objects.property(ModelSpec::class.java)

    fun model(configuration: ModelSpec.() -> Unit) {
        val spec = ModelSpec(project)
        spec.apply(configuration)
        modelSpec.set(spec)
    }

    @Nested
    val templateDirSpecs = project.objects.listProperty(TemplateDirSpec::class.java)

    fun templateDir(configuration: TemplateDirSpec.() -> Unit) {
        val spec = TemplateDirSpec(project)
        spec.apply(configuration)
        templateDirSpecs.add(spec)
    }

    @Nested
    val templateFileSpecs = project.objects.listProperty(TemplateFileSpec::class.java)

    fun templateFile(confguration: TemplateFileSpec.() -> Unit) {
        val spec = TemplateFileSpec(project)
        spec.apply(confguration)
        templateFileSpecs.add(spec)
    }

    @Nested
    val templateModuleSpecs = project.objects.listProperty(TemplateModuleSpec::class.java)

    fun templateModule(confguration: TemplateModuleSpec.() -> Unit) {
        val spec = TemplateModuleSpec(project)
        spec.apply(confguration)
        templateModuleSpecs.add(spec)
    }

    fun applyTo(task: LaplacianGenerateTask) {
        if (!modelSpec.isPresent) return
        task.modelSpec.set(modelSpec)
        task.templateDirSpecs.set(templateDirSpecs)
        task.templateFileSpecs.set(templateFileSpecs)
        task.templateModuleSpecs.set(templateModuleSpecs)
        task.prepare()
    }
}
