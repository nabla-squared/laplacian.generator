package laplacian.gradle.task.generate

import laplacian.gradle.filter.HandlebarsFilter
import laplacian.gradle.task.LaplacianGenerateTask.Companion.REPLACED_FILE_NAME
import laplacian.gradle.task.LaplacianGenerateTask.Companion.TEMPLATE_GLOB
import laplacian.gradle.task.LaplacianGenerateTask.Companion.TEMPLATE_PATTERN
import org.gradle.api.Project
import org.gradle.api.file.*
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory

open class TemplateDirSpec(
    private val project: Project
) {
    @Optional
    @OutputDirectory
    val into = project.objects
              .directoryProperty()
              .value(project.layout.projectDirectory)

    @InputDirectory
    val from = project.objects.directoryProperty()

    fun into(path: String) {
        into.set(project.layout.projectDirectory.dir(path))
    }

    fun from(path: String) {
        from.set(project.layout.projectDirectory.dir(path))
    }

    fun applyTo(copySpec: CopySpec, filterOpts: Map<String, Any>) {
        val intoDir = into.get().asFile
        val fromDir = from.get().asFile
        copySpec.into(intoDir)
        copySpec.from(fromDir) {
             it.exclude(*TEMPLATE_GLOB)
        }
        copySpec.from(fromDir) {
            it.include(*TEMPLATE_GLOB)
            it.filter(filterOpts, HandlebarsFilter::class.java)
            it.rename(TEMPLATE_PATTERN, REPLACED_FILE_NAME)
        }
    }
}
