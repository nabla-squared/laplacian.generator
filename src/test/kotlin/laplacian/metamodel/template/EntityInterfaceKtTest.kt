package laplacian.metamodel.template
import org.junit.jupiter.api.Test

class EntityInterfaceKtTest {

    val template = assertion.withTemplate(
        "template/metamodel/src/main/kotlin/{each entities.in_namespace as entity}{path entity.namespace}/model/{entity.class_name}.kt.hbs"
    )

    @Test
    fun it_generates_the_entity_interface_of_entity() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/model/Entity.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{it.name == "entity"}!!)
        }
    }

    @Test
    fun it_generates_the_entity_interface_of_property() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/model/Property.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{it.name == "property"}!!)
        }
    }

    @Test
    fun it_generates_the_entity_interface_of_relationship() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/model/Relationship.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{it.name == "relationship"}!!)
        }
    }

    @Test
    fun it_generates_the_entity_interface_of_property_mapping() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/model/PropertyMapping.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{it.name == "property_mapping"}!!)
        }
    }

    @Test
    fun it_generates_the_entity_interface_of_query() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/model/Query.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{it.name == "query"}!!)
        }
    }
}