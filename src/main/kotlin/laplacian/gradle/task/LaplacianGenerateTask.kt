package laplacian.gradle.task

import laplacian.gradle.task.generate.*
import org.gradle.api.internal.file.copy.*
import org.gradle.api.tasks.*
import java.io.File

open class LaplacianGenerateTask: AbstractCopyTask() {

    @Nested
    val modelSpec = project.objects.property(ModelSpec::class.java)

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
        modelSpec.get().applyTo(executionContext.get())
        val filterOpts = mapOf("executionContext" to executionContext.get())
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

/*
    override fun getRootSpec(): DestinationRootCopySpec {
        return super.getRootSpec() as DestinationRootCopySpec
    }

    @InputFiles
    lateinit var modelFiles: Collection<File>

    val executionContext = ExecutionContext()
    val modelLoader = project.objects
                     .property(ModelLoader::class.java)

    fun model(configurator: ModelConfigurator.() -> Unit) {
        val configuratorContext = ModelConfigurator(project, modelLoader.get())
        configuratorContext.apply(configurator)
        modelFiles = configuratorContext.getFiles()
        executionContext.baseModel = TemplateWrapper.createContext(configuratorContext.load())
    }
    */

    /*
    class ModelConfigurator(
        val project: Project,
        var loader: ModelLoader,
        val opts: MutableMap<String, Any> = mutableMapOf()
    ) {
        fun loader(loader: ModelLoader) {
            this.loader = loader
        }
        fun dir(dir:String) {
            opts.put("dir", dir)
        }
        fun include(vararg glob: String) {
            opts.put("includes", listOf(*glob))
        }
        fun exclude(vararg glob: String) {
            opts.put("excludes", listOf(*glob))
        }
        fun getFiles(): Collection<File> = project.fileTree(opts).files
        fun load(): Map<String, Any?> = this.loader.load(getFiles())
    }
    */

//     class TemplateSpecConfigurator(
//         val copySpec: CopySpec,
//         val executionContext: ExecutionContext
//     ) {
//         companion object {
//             val TEMPLATE_GLOB = arrayOf("**/*.hbs.*", "**/*.hbs")
//         }
//         fun into(targetDir: Any) {
//             copySpec.into(targetDir)
//         }
//         fun from(baseDir: Any) {
//             copySpec.from(baseDir) {
//                 it.exclude(*TEMPLATE_GLOB)
//             }
//             copySpec.from(baseDir) {
//                 it.include(*TEMPLATE_GLOB)
//                 it.filter(mapOf("executionContext" to executionContext), HandlebarsFilter::class.java)
//                 it.rename("(.*)(?:\\.hbs\\.(.+)|\\.(.+)\\.hbs)$", "$1.$2$3")
//             }
//         }
//     }

    /*
    fun template(configurator: TemplateSpecConfigurator.() -> Unit) {
        rootSpec.into(project.file("./"))
        rootSpec.exclude(".gradle/")
        rootSpec.exclude(".git/")
        val spec = rootSpec.addChild()
        val configuratorContext = TemplateSpecConfigurator(spec, executionContext)
        configuratorContext.apply(configurator)
    }

    @OutputDirectory
    fun getDestinationDir(): File? = rootSpec.destinationDir

    fun setDestinationDir(destDir: File) {
        into(destDir)
    }
    */



}


