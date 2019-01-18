package laplacian.metamodel.model

import laplacian.metamodel.template.assertion
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EntityTest {

    val model = assertion.model
    fun metamodelOfEntity() = model.entities.find{ it.name == "entity" }!!
    fun metamodelOfProperty() = model.entities.find{ it.name == "property" }!!
    fun metamodelOfRelationship() = model.entities.find{ it.name == "relationship" }!!

    @Test
    fun its_properties_can_be_accessed_with_key_accessor() {
        val entity = metamodelOfEntity()
        assertEquals("entity", entity.name)
        if (entity is Map<*,*>) {
            assertEquals("entity", entity["name"])
        }
        else {
            fail()
        }
    }

    @Test
    fun each_property_should_be_an_inherited_entity_of_the_owning_entity() {

        with(metamodelOfEntity()) {
            val nameOfEntity = properties.find{ it.name == "name" }!!
            assertEquals("Entity", nameOfEntity.entity.className)
        }

        with(metamodelOfProperty()) {
            assertTrue(inherited)
            assertEquals(1, inheritedFrom.size)
            val inheritedFrom = inheritedFrom[0]
            assertTrue(inheritedFrom.inherited)
            assertEquals("entity", inheritedFrom.propertyName)
            assertEquals("Entity", inheritedFrom.className)
        }

        with(metamodelOfRelationship()) {
            assertTrue(inherited)

            val referenceEntityRelationship = relationships.find{ it.name == "reference_entity" }!!
            assertEquals("Entity", referenceEntityRelationship.referenceEntity.className)
            assertEquals("Entity", referenceEntityRelationship.className)

            val propertyMappingsRelationship = relationships.find{ it.name == "mappings" }!!
            assertTrue(propertyMappingsRelationship.multiple)
            assertEquals("PropertyMapping", propertyMappingsRelationship.referenceEntity.className)
            assertEquals("List<PropertyMapping>", propertyMappingsRelationship.className)
        }
    }

    @Test
    fun obtaining_all_relating_entities() {
        with(metamodelOfEntity()) {
            assertEquals(listOf("Property", "Relationship", "Query"), relatingEntities.map{it.className})
            assertTrue(relatingTopLevelEntities.isEmpty())
        }
    }
}