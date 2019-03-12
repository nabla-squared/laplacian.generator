package laplacian.gradle.task

import laplacian.gradle.task.generate.*
import org.gradle.api.internal.file.copy.*
import org.gradle.api.tasks.*
import java.io.File

open class LaplacianGenerateTask: AbstractCopyTask() {

    @Nested
    val modelSpec = project.objects
                   .property(ModelSpec::class.java)
                   .value(ModelSpec(project))

    @Nested
    val templateDirSpecs = project.objects.listProperty(TemplateDirSpec::class.java)

    @Nested
    val templateFileSpecs = project.objects.listProperty(TemplateFileSpec::class.java)

    @Nested
    val templateModuleSpecs = project.objects.listProperty(TemplateModuleSpec::class.java)

    val executionContext = project.objects.property(ExecutionContext::class.java)
        .value(ExecutionContext())

    companion object {
        val TEMPLATE_GLOB = arrayOf("**/*.hbs.*", "**/*.hbs")
        val TEMPLATE_PATTERN = """(.*)(?:\.hbs\.(.+)|\.(.+)\.hbs)$""".toPattern()
        const val REPLACED_FILE_NAME = "$1.$2$3"
    }

    fun prepare() {
        rootSpec.into(project.projectDir)
        rootSpec.exclude(".gradle/")
        rootSpec.exclude(".git/")
        val context = executionContext.get()
        modelSpec.get().applyTo(context)
        context.build()
        val filterOpts = mapOf("executionContext" to context)
        templateDirSpecs.get().forEach { spec ->
            spec.applyTo(rootSpec.addChild(), filterOpts)
        }
        templateFileSpecs.get().forEach { spec ->
            spec.applyTo(rootSpec.addChild(), filterOpts)
        }
        templateModuleSpecs.get().forEach { spec ->
            spec.applyTo(rootSpec.addChild(), filterOpts)
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


