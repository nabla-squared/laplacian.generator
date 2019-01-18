package laplacian.metamodel.template

import org.junit.jupiter.api.Test

class TemplateAssertionKtTest {
    val template = assertion.withTemplate(
        "template/metamodel/src/test/kotlin/{path project.namespace}/{upper-camel project.name}TemplateAssertion.kt.hbs"
    )
    @Test
    fun it_generates_the_entity_interface_of_entity() {
        val toBeFile = "src/test/kotlin/laplacian/metamodel/MetamodelTemplateAssertion.kt"
        template.assertSameContent(toBeFile)
    }
}
