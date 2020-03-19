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
        modelSpec.get().apply(configuration)
    }

    val target = project.objects
                .fileProperty()
                .value { project.projectDir }

    @Nested
    val templateSpec = project.objects
                      .property(TemplateSpec::class.java)
                      .value(TemplateSpec(project))

    val templateSources = templateSpec.map { it.files }

    fun template(configuration: TemplateSpec.() -> Unit) {
        templateSpec.get().apply(configuration)
    }

    fun applyTo(task: LaplacianGenerateTask) {
        task.target.set(target.get().asFile)
        task.modelSpec.set(modelSpec)
        task.templateSpec.set(templateSpec)
        task.prepare()
    }
}
