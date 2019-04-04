package laplacian.gradle.task.generate

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

class FileResourceSpecBase(
    override val project: Project,
    defaultFiles: Array<String>,
    defaultConfigurationName: String
) : FileResourceSpec {
    @InputFiles
    override val files = project.files(defaultFiles)

    @Optional
    @Input
    override val moduleNames = project.objects.listProperty(String::class.java)

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
        moduleNames.add("/${name}-${version}.jar")
    }
    override fun forEachFileSets(consumer: (fileSet: FileCollection) -> Unit) {
        consumer(files.asFileTree)
        val configuration = configuration.get()
        moduleNames.get().forEach { path ->
            val archive = configuration.files.find{ it.absolutePath.endsWith(path) }
            val content = project.zipTree(archive).asFileTree
            content.forEach { it.absolutePath } // a workaround extract the all files in the archive
            consumer(content)
        }
    }
}
