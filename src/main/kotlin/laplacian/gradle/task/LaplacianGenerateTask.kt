package laplacian.gradle.task

import laplacian.gradle.task.generate.*
import org.gradle.api.internal.file.copy.*
import org.gradle.api.tasks.*
import org.slf4j.LoggerFactory

open class LaplacianGenerateTask: AbstractCopyTask() {

    @Nested
    val modelSpec = project.objects
       .property(ModelSpec::class.java)
       .value(ModelSpec(project))

    @Nested
    val templateSpecs = project.objects
       .listProperty(TemplateSpec::class.java)

    val executionContext = project.objects
       .property(ExecutionContext::class.java)
       .value(ExecutionContext())

    fun prepare() {
        rootSpec.into(project.projectDir)
        rootSpec.exclude(".gradle/")
        rootSpec.exclude(".git/")
        rootSpec.exclude("build/")
        val context = executionContext.get()
        modelSpec.get().applyTo(context)
        context.build()
        if (LOG.isInfoEnabled) LOG.info("Generate based on the following model: ${context.currentModel}")
        templateSpecs.get().forEach { spec ->
            if (LOG.isInfoEnabled) LOG.info("Use the following template: $spec")
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


