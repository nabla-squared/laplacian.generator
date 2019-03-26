package laplacian.gradle.task.generate

import laplacian.gradle.filter.HandlebarsFilter
import laplacian.gradle.task.LaplacianGenerateTask.Companion.REPLACED_FILE_NAME
import laplacian.gradle.task.LaplacianGenerateTask.Companion.TEMPLATE_GLOB
import laplacian.gradle.task.LaplacianGenerateTask.Companion.TEMPLATE_PATTERN
import org.gradle.api.Project
import org.gradle.api.file.*
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory

class TemplateSpec(
    project: Project,
    val base: FileResourceSpec = FileResourceSpecBase(
        project, arrayOf("template"), "template"
    )
) : FileResourceSpec by base  {

    @Optional
    @OutputDirectory
    val into = project.objects
              .property(String::class.java)
              .value("./")

    fun into(path: String) {
        into.set(path)
    }

    fun applyTo(copySpec: CopySpec, filterOpts: Map<String, Any>) {
        base.forEachFileSets { templateFiles ->
            copySpec.into(into.get())
            copySpec.from(templateFiles) {
                it.exclude(*TEMPLATE_GLOB, "META-INF/**")
            }
            copySpec.from(templateFiles) {
                it.include(*TEMPLATE_GLOB)
                it.exclude("META-INF/**")
                it.filter(filterOpts, HandlebarsFilter::class.java)
                it.rename(TEMPLATE_PATTERN, REPLACED_FILE_NAME)
            }
        }
    }
}
