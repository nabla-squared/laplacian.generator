package laplacian.gradle.task.generate

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException

class FileResourceSpecBase(
    override val project: Project,
    defaultConfigurationName: String
) : FileResourceSpec {
    companion object {
        val LOG = LoggerFactory.getLogger(FileResourceSpecBase::class.java)
    }
    @InputFiles
    override val files = project.files(emptyArray<String>())

    @Optional
    @Input
    override val moduleNames = project.objects
        .listProperty(String::class.java)
        .empty()

    @Input
    override val configurationName = project.objects
                           .property(String::class.java)
                           .value(defaultConfigurationName)

    override val configuration = configurationName.map {
        project.configurations.getByName(it)
    }

    override fun from(vararg paths: Any) {
        files.from(*paths)
    }

    override fun module(module: Dependency) {
        val name = module.name
        val version = module.version
        val modulePath = "/${name}-${version}.jar"
        if (LOG.isInfoEnabled) LOG.info(
            """>>>>${moduleNames.get().map{it}.joinToString(",")} Loading into the "${configurationName.get()}" configuration the module at: $modulePath"""
        )
        moduleNames.add(modulePath)
    }

    override fun forEachFileSets(consumer: (fileSet: FileCollection) -> Unit) {
        val configuration = configuration.get()
        moduleNames.get().forEach { path ->
            val archivePaths = configuration.files.map{ it.absolutePath }
            val archive = archivePaths.find{ it.endsWith(path) } ?: throw IllegalStateException(
                "It seems that one of the dependent laplacian-module `$path` could not be found in ($archivePaths)."
            )
            val content = project.zipTree(archive).asFileTree
            consumer(content)
        }
        consumer(files.asFileTree)
    }

    override fun toString() = mapOf(
        "modules" to moduleNames.get(),
        "files" to files.map{it.absolutePath}
    ).toString()
}
