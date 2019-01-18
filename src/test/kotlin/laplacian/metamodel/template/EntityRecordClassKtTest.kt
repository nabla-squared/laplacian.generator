package laplacian.metamodel.template

import org.junit.jupiter.api.Test

class EntityRecordClassKtTest {

    val template = assertion.withTemplate(
      "template/metamodel/src/main/kotlin/{each entities.in_namespace as entity}{path entity.namespace}/record/{entity.class_name}Record.kt.hbs"
    )

    @Test
    fun it_generates_the_record_class_of_entity_entity() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/record/EntityRecord.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{ it.name == "entity" }!!)
        }
    }

    @Test
    fun it_generates_the_record_class_of_property_entity() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/record/PropertyRecord.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{ it.name == "property" }!!)
        }
    }

    @Test
    fun it_generates_the_record_class_of_relationship_entity() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/record/RelationshipRecord.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{ it.name == "relationship" }!!)
        }
    }

    @Test
    fun it_generates_the_record_class_of_property_mappings_entity() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/record/PropertyMappingRecord.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{ it.name == "property_mapping" }!!)
        }
    }

    @Test
    fun it_generates_the_record_class_of_query_entity() {
        val toBeFile = "src/main/kotlin/laplacian/metamodel/record/QueryRecord.kt"
        template.assertSameContent(toBeFile) { model ->
            model + ("entity" to model.entities.find{ it.name == "query" }!!)
        }
    }
}