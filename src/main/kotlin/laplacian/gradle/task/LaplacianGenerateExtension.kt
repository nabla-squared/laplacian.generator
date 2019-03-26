package laplacian.gradle.task

import laplacian.gradle.task.generate.ModelSpec
import laplacian.gradle.task.generate.TemplateSpec
import org.gradle.api.Project
import org.gradle.api.tasks.*

open class LaplacianGenerateExtension constructor(
    private val project: Project
) {
    @Nested
    val modelSpec = project.objects
                   .property(ModelSpec::class.java)
                   .value(ModelSpec(project))

    fun model(configuration: ModelSpec.() -> Unit) {
        val spec = modelSpec.get()
        spec.apply(configuration)
    }

    @Nested
    val templateSpecs = project.objects
                          .listProperty(TemplateSpec::class.java)

    fun template(configuration: TemplateSpec.() -> Unit) {
        val spec = TemplateSpec(project)
        spec.apply(configuration)
        templateSpecs.add(spec)
    }

    fun applyTo(task: LaplacianGenerateTask) {
        task.modelSpec.set(modelSpec)
        task.templateSpecs.set(templateSpecs)
        task.prepare()
    }
}
