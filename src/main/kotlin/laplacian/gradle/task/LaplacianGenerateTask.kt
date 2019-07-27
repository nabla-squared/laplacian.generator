package laplacian.gradle.task

import laplacian.gradle.task.generate.*
import org.gradle.api.file.FileTree
import org.gradle.api.internal.file.copy.*
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.slf4j.LoggerFactory
import java.io.File

open class LaplacianGenerateTask: AbstractCopyTask() {

    @Nested
    val modelSpec = project.objects
        .property(ModelSpec::class.java)
        .value(ModelSpec(project))

    @InputFiles
    val modelFiles: Provider<FileTree> = modelSpec.map{ spec -> spec.files.asFileTree }

    @Nested
    val templateSpec = project.objects
        .property(TemplateSpec::class.java)
        .value(TemplateSpec(project))

    val executionContext = project.objects
        .property(ExecutionContext::class.java)
        .value(ExecutionContext())

    val target = project.objects
        .property(File::class.java)
        .value(project.projectDir)

    fun prepare() {
        rootSpec.exclude(".gradle/")
        rootSpec.exclude(".git/")
        rootSpec.exclude("build/")
        val context = executionContext.get()
        modelSpec.get().applyTo(context)
        context.build()
        if (LOG.isInfoEnabled) LOG.info(
            "generate into ${target.get().absolutePath} based on the following model: ${context.currentModel}"
        )
        templateSpec.get().also { spec ->
            if (LOG.isInfoEnabled) LOG.info(
                "Use the following template: $spec"
            )
            spec.baseDir.set(target)
            spec.applyTo(rootSpec.addChild(), context)
        }
    }

    override fun createCopyAction(): CopyAction {
        return DynamicFileStructureCopyAction(
            fileLookup.getFileResolver(project.projectDir),
            executionContext.get()
        )
    }

    override fun createRootSpec(): CopySpecInternal {
        val rootSpec = instantiator.newInstance(
            DestinationRootCopySpec::class.java, fileResolver, super.createRootSpec()
        )
        return rootSpec
    }

    companion object {
        val LOG = LoggerFactory.getLogger(LaplacianGenerateTask::class.java)
    }
}


