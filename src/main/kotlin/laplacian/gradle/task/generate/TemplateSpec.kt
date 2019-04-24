package laplacian.gradle.task.generate

import laplacian.gradle.filter.*
import org.gradle.api.Project
import org.gradle.api.file.*
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.slf4j.LoggerFactory

class TemplateSpec(
    project: Project,
    val base: FileResourceSpec = FileResourceSpecBase(
        project, arrayOf("template"), "template"
    )
) : FileResourceSpec by base  {

    override fun toString() = base.toString()

    companion object {
        val EXCLUDED_PATTERNS = listOf(
            """^META-INF/""".toRegex(),
            """(^|[./])partial(?=[./]).*\.hbs(\.|$)""".toRegex()
        )
        val LOG = LoggerFactory.getLogger(TemplateSpec::class.java)
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
            if (LOG.isInfoEnabled) LOG.info("Processing a template: ${detail.sourceName} -> ${detail.name}")
            doForEachFile(detail, context)
        }
        base.forEachFileSets { templateFiles ->
            if (LOG.isInfoEnabled) LOG.info("Template files: ${templateFiles.map{ it.absolutePath }}")
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
            HandlebarsCopyHandler(),
            PlantUmlCopyHandler(),
            IncludesHandler(),
            ExecPermissionHandler()
        ).filter { handler ->
            handler.handle(fileCopyDetails, context)
        }
        fileCopyDetails.filter(
            mapOf("fileCopyPipeline" to pipeline),
            FileCopyPipelineFilter::class.java
        )
    }
}
