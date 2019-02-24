package laplacian.gradle.task

import laplacian.gradle.GeneratorPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class LaplacianGenerateTaskTest {
    @Test
    fun testApplyPluginWhenLoadingProject() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(GeneratorPlugin::class.java)
    }
}
