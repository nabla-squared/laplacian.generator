package laplacian.gradle.task.generate

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

interface FileResourceSpec {
    val project: Project
    val files: ConfigurableFileCollection
    val moduleNames: ListProperty<String>
    val configurationName: Property<String>
    val configuration: Provider<Configuration>
    fun from(vararg paths: Any)
    fun module(module: Dependency)
    fun forEachFileSets(consumer: (fileSet: FileCollection) -> Unit)
}

