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

    @Optional
    @OutputDirectory
    val into = project.objects
              .property(String::class.java)
              .value("./")

    fun into(path: String) {
        into.set(path)
    }

    fun applyTo(copySpec: CopySpec, context: ExecutionContext) {
        val fileCopyPipeline = listOf(
            HandlebarsCopyHandler(context),
            PlantUmlCopyHandler()
        )
        base.forEachFileSets { templateFiles ->
            copySpec.into(into.get())
            copySpec.from(templateFiles) {
                it.eachFile { fileCopyDetails ->
                    context.fileCopyDetails = fileCopyDetails
                    if (fileCopyDetails.sourcePath.startsWith("META-INF/")) {
                        fileCopyDetails.exclude()
                        return@eachFile
                    }
                    val pipeline = fileCopyPipeline.filter { handler ->
                        handler.handle(fileCopyDetails)
                    }
                    fileCopyDetails.filter(
                        mapOf("fileCopyPipeline" to pipeline),
                        FileCopyPipelineFilter::class.java
                    )
                }
            }
        }
    }
}
