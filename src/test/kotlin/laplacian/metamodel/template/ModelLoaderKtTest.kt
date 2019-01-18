package laplacian.metamodel.template

import org.junit.jupiter.api.Test

class ModelLoaderKtTest {
    val template = assertion.withTemplate(
        "template/metamodel/src/main/kotlin/{path project.namespace}/{upper-camel project.name}ModelLoader.kt.hbs"
    )
    @Test
    fun it_generates_the_map_record_based_repository_class() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/MetamodelModelLoader.kt"
        template.assertSameContent(toBeFile)
    }
}
