package laplacian.metamodel.template

import org.junit.jupiter.api.Test

class ModelKtTest {

    val template = assertion.withTemplate(
        "template/metamodel/src/main/kotlin/{path project.namespace}/{upper-camel project.name}Model.kt.hbs"
    )

    @Test
    fun it_generates_the_map_record_based_repository_class() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/MetamodelModel.kt"
        template.assertSameContent(toBeFile)
    }
}
