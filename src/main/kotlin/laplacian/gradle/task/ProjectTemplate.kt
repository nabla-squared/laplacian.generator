package laplacian.gradle.task

import com.github.jknack.handlebars.Context
import laplacian.ModelLoader
import laplacian.gradle.filter.HandlebarsFilter
import laplacian.util.TemplateWrapper
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.internal.file.copy.*
import org.gradle.api.tasks.*
import java.io.File

open class ProjectTemplate : AbstractCopyTask() {

    @InputFiles
    lateinit var modelFiles: Collection<File>

    val executionContext = ExecutionContext()

    fun model(configurator: ModelConfigurator.() -> Unit) {
        val configuratorContext = ModelConfigurator(project)
        configuratorContext.apply(configurator)
        modelFiles = configuratorContext.getFiles()
        executionContext.baseModel = TemplateWrapper.createContext(configuratorContext.load())
    }

    class ModelConfigurator(val project: Project) {
        val opts: MutableMap<String, Any> = mutableMapOf()
        var _loader: ModelLoader<*>? = null
        fun loader(loader: ModelLoader<*>) {
            _loader = loader
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
        fun load(): Map<String, Any?> = _loader!!.load(getFiles())
    }

    class ExecutionContext {
        var baseModel = TemplateWrapper.createContext(emptyMap<String, Any?>())
        private var _currentModel: Context? = null
        var currentModel: Context
            get() = _currentModel ?: baseModel
            set(model) {
                _currentModel = model
            }
    }

    class TemplateSpecConfigurator(
        val copySpec: CopySpec,
        val executionContext: ExecutionContext
    ) {
        companion object {
            val TEMPLATE_GLOB = arrayOf("**/*.hbs.*", "**/*.hbs")
        }
        fun into(targetDir: String) {
            copySpec.into(targetDir)
        }
        fun from(baseDir: String) {
            copySpec.from(baseDir) {
                it.exclude(*TEMPLATE_GLOB)
            }
            copySpec.from(baseDir) {
                it.include(*TEMPLATE_GLOB)
                it.filter(mapOf("executionContext" to executionContext), HandlebarsFilter::class.java)
                it.rename("(.*)(?:\\.hbs\\.(.+)|\\.(.+)\\.hbs)$", "$1.$2$3")
            }
        }
    }

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

    override fun createCopyAction(): CopyAction {
        val destinationDir = getDestinationDir() ?: throw InvalidUserDataException(
            "No copy destination directory has been specified, use 'into' to specify the target directory."
        )
        return DynamicFileStructureCopyAction(
            fileLookup.getFileResolver(destinationDir), executionContext
        )
    }

    override fun createRootSpec(): CopySpecInternal {
        return instantiator.newInstance(
            DestinationRootCopySpec::class.java, fileResolver, super.createRootSpec()
        )
    }

    override fun getRootSpec(): DestinationRootCopySpec {
        return super.getRootSpec() as DestinationRootCopySpec
    }

}


