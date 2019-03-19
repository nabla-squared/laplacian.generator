package laplacian.gradle.task.generate

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ProjectTest {

    @Test
    fun test_default_namespace_construction() {
        assertEquals(
            "laplacian.example.test_project",
            Project(project).namespace
        )
    }
    private val project = mapOf(
        "group" to "laplacian",
        "type" to "example",
        "name" to "test-project"
    )

    @Test
    fun test_namespace_of_project_with_subname() {
         assertEquals(
            "laplacian.example.test_project.sub_name",
            Project(projectWithSubname).namespace
        )
    }
    private val projectWithSubname = project + ("subname" to "sub-name")

    @Test
    fun test_the_map_entry_which_has_key_namespace_is_used_if_it_exists() {
        assertEquals(
            "com.exmaple.namespace",
            Project(projectDefinedExplicitNamespace).namespace
        )
    }
    private val projectDefinedExplicitNamespace = project + ("namespace" to "com.exmaple.namespace")
}
