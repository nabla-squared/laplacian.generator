package laplacian.metamodel.template

import org.junit.jupiter.api.Test

class EntityListKtTest {

    val template = assertion.withTemplate(
        "template/metamodel/src/main/kotlin/{each entities.in_namespace as entity}{path entity.namespace}/model/{if entity.top_level}{entity.class_name}List.kt.hbs"
    )

    @Test
    fun it_generates_the_list_wrapper_class_of_entity() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/model/EntityList.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{it.name == "entity"}!!)
        }
    }
}