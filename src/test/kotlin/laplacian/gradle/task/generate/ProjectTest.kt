package laplacian.gradle.task.generate

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ProjectTest {

    @Test
    fun test_default_namespace_construction() {
        val projectModel = Project(project)
        assertEquals(
            "laplacian.test_project",
            Project(projectModel).namespace
        )
        assertEquals(0, projectModel.models.size)
        assertEquals(0, projectModel.templates.size)
    }
    private val project = mapOf(
        "group" to "laplacian",
        "type" to "model",
        "name" to "test-project"
    )

    @Test
    fun test_namespace_of_project_with_subname() {
         assertEquals(
            "laplacian.test_project.sub_name",
            Project(projectWithSubname).namespace
        )
    }

    @Test
    fun test_module_id_is_a_namespace_with_tokens_separated_with_hyphen() {
        assertEquals(
            "laplacian.model.test-project.sub-name",
            Project(projectWithSubname).moduleId
        )
    }
    private val projectWithSubname = project + ("subname" to "sub-name")

    @Test
    fun test_a_project_can_have_dependent_models_and_templates() {
        val project = Project(projectWithDependentModules)
        assertEquals(2, project.models.size)
        assertEquals("external:external.model.model-1:2.0.0", project.models.first().artifactId)
        assertEquals("external:external.model.model-2:1.0.0", project.models.last().artifactId)
    }
    private val dependentModels = listOf(
        mapOf("name" to "model-1", "group" to "external", "version" to "2.0.0"),
        mapOf("name" to "model-2", "group" to "external", "version" to "1.0.0")
    )
    private val projectWithDependentModules = project + ("models" to dependentModels)

    @Test
    fun test_the_map_entry_which_has_key_namespace_is_used_if_it_exists() {
        assertEquals(
            "com.exmaple.namespace",
            Project(projectDefinedExplicitNamespace).namespace
        )
    }
    private val projectDefinedExplicitNamespace = project + ("namespace" to "com.exmaple.namespace")

    @Test
    fun test_list_dependent_plugins_excluding_self() {
        val project = Project(plugin_project)
        assertEquals(4, project.plugins.size)
        assertEquals(3, project.pluginsExcludingSelf.size)
    }
    private val dependengPlugins = listOf(
        mapOf("name" to "plugin-1", "group" to "external", "version" to "2.0.0"),
        mapOf("name" to "plugin-1", "group" to "external", "version" to "2.0.0"),
        mapOf("name" to "plugin-2", "group" to "external", "version" to "1.0.0"),
        mapOf("name" to "test-plugin", "group" to "laplacian", "version" to "1.0.0")
    )
    private val plugin_project = mapOf(
        "group" to "laplacian",
        "type" to "plugin",
        "name" to "test-plugin",
        "version" to "1.0.1",
        "plugins" to dependengPlugins
    )
}
