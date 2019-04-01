package laplacian.gradle.task

import laplacian.gradle.task.generate.*
import org.gradle.api.internal.file.copy.*
import org.gradle.api.tasks.*

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
        val context = executionContext.get()
        modelSpec.get().applyTo(context)
        context.build()
        templateSpecs.get().forEach { spec ->
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
}


