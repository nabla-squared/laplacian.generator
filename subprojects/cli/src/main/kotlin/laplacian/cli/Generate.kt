package laplacian.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import laplacian.generate.ExecutionContext
import laplacian.generate.ModelEntryResolver
import laplacian.generate.copyhandler.*
import laplacian.generate.expression.ExpressionProcessor
import laplacian.handlebars.HandlebarsExtension
import org.pf4j.DefaultPluginManager
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

class Generate: CliktCommand(help = GENERATE_COMMAND_HELP) {

    val plugins: List<String>
        by option("-p", "--plugin", help = PLUGIN_OPTION_HELP)
            .multiple(default = emptyList())

    val schema: String
        by option("-c", "--schema", help = SCHEMA_OPTION_HELP)
            .default("")

    val models: List<String>
        by option("-m", "--model", help = MODEL_OPTION_HELP)
            .multiple(default = listOf(DEFAULT_MODEL_DIR))

    val jsonModels: List<String>
        by option("-m", "--json-model", help = JSON_MODEL_OPTION_HELP)
            .multiple(default = emptyList())

    val templates: List<String>
        by option("-t", "--template", help = TEMPLATE_OPTION_HELP)
            .multiple(default = listOf(DEFAULT_TEMPLATE_DIR))

    val destination: String
        by option("-d", "--destination", help = DESTINATION_OPTION_HELP)
            .default("./dest")

    val noCache: Boolean
        by option("-C", "--no-cache", help = NO_CACHE_OPTION_HELP)
            .flag()

    lateinit var executionContext: ExecutionContext

    override fun run() {
        executionContext = ExecutionContext()
        executionContext.addModel(mapOf("laplacian" to mapOf("args" to mapOf("plugins" to plugins))))
        loadPlugins()
        createWorkDir()
        val destDir = createDestDir()
        val modelRootDirs = readModulesFrom(models)
        processModelDir(modelRootDirs)
        jsonModels.forEach { executionContext.addModel(it) }
        executionContext.build()
        val templateRootDirs = readModulesFrom(templates)
        val copyDetails = processTemplates(templateRootDirs)
        val includes = copyDetails.fold(mutableMapOf<String, MutableList<FileCopyDetails>>()) { acc, fileCopy ->
            if (fileCopy.includeName == null) {
                return@fold acc
            }
            val includesForPath = acc.getOrPut(fileCopy.destPath) { mutableListOf<FileCopyDetails>() }
            includesForPath.add(fileCopy)
            acc
        }
        copyDetails.forEach {
            it.copyTo(
                destDir,
                includes.getOrDefault(it.destPath, emptyList<FileCopyDetails>())
            )
        }
    }

    private fun loadPlugins() = DefaultPluginManager().let { manager ->
        (plugins + PLUGIN_ROOT_DIR).forEach {
            if (isRemoteModule(it)) {
                val plugin = downloadRemoteModuleToCache(URL(it))
                manager.loadPlugin(plugin.toPath())
                return@forEach
            }
            val path = Path.of(it)
            if (Files.isDirectory(path)) {
                LOG.info("Loading plugins under the directory: $path")
                path.toFile().listFiles().forEach { jar ->
                    if (jar.isFile && jar.extension == "jar") {
                        LOG.info(">> Loading plugin at: ${jar.name}")
                        manager.loadPlugin(jar.toPath())
                    }
                }
                return@forEach
            }
            if (Files.isRegularFile(path)) {
                LOG.info("Loading plugin at: $path")
                manager.loadPlugin(path)
                return@forEach
            }
            throw IllegalArgumentException(
                "Plugin path should be a local file or directory path or a URL."
            )
        }
        manager.startPlugins()
        manager.getExtensions(HandlebarsExtension::class.java).forEach {
            executionContext.addHandlebarsExtension(it)
        }
        manager.getExtensions(ModelEntryResolver::class.java).forEach {
            executionContext.addModelEntryResolver(it)
        }
    }

    private fun readModulesFrom(modulePaths: List<String>): List<File> = modulePaths.map { path ->
        when {
            isRemoteModule(path) -> readRemoteModule(URL(path))
            isLocalModule(path) -> extractModule(File(path))
            isLocalDir(path) -> File(path)
            else -> throw IllegalArgumentException(
                "The given path could not be read as a module.: $path"
            )
        }
    }

    private fun isRemoteModule(path: String): Boolean =
        path.contains("""^https?://""".toRegex())

    private fun downloadRemoteModuleToCache(moduleUrl: URL): File {
        val moduleName = moduleUrl.path.substringAfterLast("/")
        if (moduleName.isEmpty()) throw IllegalArgumentException(
            "Invalid module url [${moduleUrl}]."
        )
        val moduleFile = File(MODULE_CACHE_DIR, moduleName)
        if (moduleFile.exists() && !noCache) return moduleFile
        if (!moduleFile.exists() && !moduleFile.createNewFile()) throw IllegalStateException(
            "Failed to create new module cache file at [${moduleFile.absolutePath}]."
        )
        moduleUrl.openStream().copyTo(moduleFile.outputStream(), BUFFER_SIZE)
        return moduleFile
    }

    private fun readRemoteModule(moduleUrl: URL): File {
        val cacheFile = downloadRemoteModuleToCache(moduleUrl)
        return extractModule(cacheFile)
    }

    private fun isLocalModule(path: String): Boolean =
        File(path).let{ it.isFile && MODULE_EXTENSIONS.contains(it.extension) }

    private fun extractModule(moduleFile: File): File {
        val moduleDir = File(MODULE_CACHE_DIR, moduleFile.nameWithoutExtension)
        if (moduleDir.exists() && !moduleDir.deleteRecursively()) throw IllegalStateException(
            "Failed to delete cache directory. [${moduleDir.absolutePath}]"
        )
        if (!moduleDir.exists() && !moduleDir.mkdirs()) throw IllegalStateException(
            "Failed to extract the content of a local module at [${moduleFile.absolutePath}] into [${moduleDir.absolutePath}]"
        )
        ZipInputStream(moduleFile.inputStream()).use { zip ->
            val moduleDirPath = moduleDir.canonicalPath + File.separator
            var zipEntry = zip.nextEntry
            while (zipEntry != null) {
                if (zipEntry.isDirectory) {
                    zipEntry = zip.nextEntry
                    continue
                }
                val destFile = File(moduleDir, zipEntry.name)
                if (!destFile.canonicalPath.startsWith(moduleDirPath)) throw IllegalStateException(
                    "Illegal zip entry: ${zipEntry.name}"
                )
                val destDir = destFile.parentFile
                if (!destDir.exists() && !destDir.mkdirs()) throw IllegalStateException(
                    "Failed to create directory [${destDir.absolutePath}]" +
                    " while extracting the content of a local module at [${moduleFile.absolutePath}] " +
                    " into [${moduleDir.absolutePath}]."
                )
                LOG.info("writing ${zipEntry.name} to ${destFile.absolutePath}")
                destFile.outputStream().use {
                    zip.copyTo(it, BUFFER_SIZE)
                }
                zipEntry = zip.nextEntry
            }
        }
        return moduleDir
    }

    private fun isLocalDir(path: String): Boolean =
        File(path).let{ it.isDirectory }

   private fun processTemplates(templateDirs: List<File>): List<FileCopyDetails> =
        templateDirs.fold(emptyList<FileCopyDetails>()) { acc, templateFile ->
            acc + processTemplateDir(templateFile)
        }

    private fun createDestDir(): File {
        val destDir = File(destination)
        if (destDir.exists() && !destDir.deleteRecursively()) throw IllegalStateException(
            "Failed to clean up existing destination directory.: $destination"
        )
        if (!destDir.mkdirs()) throw IllegalStateException(
            "Could not create the destination directory.: $destination"
        )
        return destDir
    }

    private fun createWorkDir() {
        if (!MODULE_CACHE_DIR.exists() && !MODULE_CACHE_DIR.mkdirs()) throw IllegalStateException(
            "Failed to create module cache directory at [${MODULE_CACHE_DIR.absolutePath}]."
        )
    }

    private fun processModelDir(modelDirs: List<File>) {
        //if (schemaFile.isPresent) executionContext.setModelSchema(schemaFile.asFile.get())
        //executionContext.addModelEntryResolver(*modelEntryResolvers.get().toTypedArray())
        val modelFiles = modelDirs.flatMap { modelDir ->
            modelDir.walkTopDown().filter { it.isFile }.toList().filter {
                it.name.contains(SUPPORTED_MODEL_FILE_FORMATS)
            }
        }
        executionContext.addModel(*modelFiles.sorted().toTypedArray())
    }

    private fun processTemplateDir(templateDir: File): List<FileCopyDetails> {
        if (!templateDir.exists() || !templateDir.isDirectory) throw IllegalStateException(
            "Template directory does not exists or is not a directory: ${templateDir.absolutePath}"
        )
        val templateFiles = templateDir.walkTopDown().filter { it.isFile }.toList()
        return templateFiles.flatMap { templateFile ->
            val templateFileRelPath = templateFile.toRelativeString(templateDir)
            val template = templateFile.readText()
            val expandedPaths = ExpressionProcessor.process(
                templateFileRelPath, executionContext
            )
            expandedPaths.map { (destFilePath, context) ->
                val destFileName = destFilePath.substringAfterLast("/")
                val destFileDir = destFilePath.substringBeforeLast("/", "")
                executionContext.currentModel = context
                FileCopyDetails(
                    templateFile = templateFile,
                    template = template,
                    context = executionContext,
                ).also {
                    it.destFileName = destFileName
                    it.destFileDir = destFileDir
                    for (handler in COPY_HANDLERS) {
                        val doNext = handler.handle(it)
                        if (!doNext) break
                    }
                }
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(Generate::class.java)
        val COPY_HANDLERS = listOf(
            IgnoreFileHandler(),
            BinaryFileHandler(),
            HandlebarsCopyHandler(),
            PlantUmlCopyHandler(),
            IncludesHandler(),
            ExecPermissionHandler(),
        )
        const val HOME_ENV_NAME = "HOME"
        val PLUGIN_ROOT_DIR = "${System.getenv(HOME_ENV_NAME)}/.laplacian/plugin"
        const val BUFFER_SIZE = 8 * 1024
        val SUPPORTED_MODEL_FILE_FORMATS = """\.(yaml|yml|json|js)$""".toRegex()
        const val DEFAULT_TEMPLATE_DIR = "./template"
        const val DEFAULT_MODEL_DIR = "./model"
        val MODULE_EXTENSIONS = listOf("zip", "jar")
        val MODULE_CACHE_DIR = File("${System.getProperty("user.home")}/.laplacian/cache")
        const val GENERATE_COMMAND_HELP =
            "Generates resources from model data applying template files."
        const val PLUGIN_OPTION_HELP =
            "Path to a directory or an archive which contains plugin module."
        const val MODEL_OPTION_HELP =
            "Path to a directory or an archive which contains generator model files."
        const val TEMPLATE_OPTION_HELP =
            "Path to a directory or an archive which contains generator template files."
        const val SCHEMA_OPTION_HELP =
            "Path to the json schema file."
        const val DESTINATION_OPTION_HELP =
            "Path to the directory which the generated files put into."
        const val NO_CACHE_OPTION_HELP =
            "All cached modules and plugins will be ignored and replaced with the newly loaded ones."
        const val JSON_MODEL_OPTION_HELP =
            "JSON or YAML format model data."
    }
}