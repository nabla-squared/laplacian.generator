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
    val into = project.objects.directoryProperty()

    fun into(dest: Any) {
        into.set(project.file(dest))
    }

    fun applyTo(copySpec: CopySpec, filterOpts: Map<String, Any>) {
        loadFromModules()
        val targetDir = into.asFile.getOrElse(project.file("/"))
        val templateFiles = files.asFileTree
        copySpec.into(targetDir)
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
