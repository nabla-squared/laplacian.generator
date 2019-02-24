package laplacian.gradle.task.generate

import laplacian.gradle.filter.HandlebarsFilter
import laplacian.gradle.task.LaplacianGenerateTask.Companion.REPLACED_FILE_NAME
import laplacian.gradle.task.LaplacianGenerateTask.Companion.TEMPLATE_PATTERN
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile

open class TemplateFileSpec(
    private val project: Project
) {
    @OutputFile
    val into = project.objects.fileProperty()

    @InputFile
    val from = project.objects.fileProperty()

    fun into(path: String) {
        into.set(project.layout.projectDirectory.file(path))
    }

    fun from(path: String) {
        from.set(project.layout.projectDirectory.file(path))
    }

    fun applyTo(copySpec: CopySpec, filterOpts: Map<String, Any>) {
        val intoFile = into.get().asFile
        val fromFile = from.get().asFile
        copySpec.into(intoFile)
        copySpec.from(fromFile) {
            if (fromFile.name.contains("""\.hbs\.?""")) {
                it.filter(filterOpts, HandlebarsFilter::class.java)
                it.rename(TEMPLATE_PATTERN, REPLACED_FILE_NAME)
            }
        }
    }
}
