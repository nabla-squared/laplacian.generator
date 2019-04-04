package laplacian.gradle.task.generate

import laplacian.gradle.filter.*
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

    companion object {
        val EXCLUDED_PATTERNS = listOf(
            """^META-INF/""".toRegex(),
            """(^|[./])partial(?=[./]).*\.hbs(\.|$)""".toRegex()
        )
    }

    @Optional
    @OutputDirectory
    val into = project.objects
              .property(String::class.java)
              .value("./")

    fun into(path: String) {
        into.set(path)
    }

    fun applyTo(copySpec: CopySpec, context: ExecutionContext) {
        copySpec.includeEmptyDirs = false
        copySpec.into(into.get())
        copySpec.eachFile { detail ->
            doForEachFile(detail, context)
        }
        base.forEachFileSets { templateFiles ->
            copySpec.from(templateFiles)
        }
    }

    private fun doForEachFile(fileCopyDetails: FileCopyDetails, context: ExecutionContext) {
        context.currentTemplate = fileCopyDetails.file
        if (EXCLUDED_PATTERNS.any{ it.containsMatchIn(fileCopyDetails.sourcePath) }) {
            fileCopyDetails.exclude()
            return
        }
        val pipeline = listOf(
            HandlebarsCopyHandler(context),
            PlantUmlCopyHandler()
        ).filter { handler ->
            handler.handle(fileCopyDetails)
        }
        fileCopyDetails.filter(
            mapOf("fileCopyPipeline" to pipeline),
            FileCopyPipelineFilter::class.java
        )
    }
}
