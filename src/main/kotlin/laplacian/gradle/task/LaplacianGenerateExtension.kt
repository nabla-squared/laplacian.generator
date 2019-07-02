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
    val templateSpec = project.objects
                      .property(TemplateSpec::class.java)
                      .value(TemplateSpec(project))

    val templateSource = templateSpec.map { it.files }

    fun template(configuration: TemplateSpec.() -> Unit) {
        val spec = TemplateSpec(project)
        spec.apply(configuration)
        templateSpec.set(spec)
    }

    fun applyTo(task: LaplacianGenerateTask) {
        task.modelSpec.set(modelSpec)
        task.templateSpec.set(templateSpec)
        task.prepare()
    }
}
