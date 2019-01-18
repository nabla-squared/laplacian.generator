package laplacian.metamodel.model

import laplacian.metamodel.MetamodelModel
import laplacian.metamodel.MetamodelTemplateAssertion
import org.junit.jupiter.api.Test


class RelationshipTest {

    val recordClassTemplate =
        "template/metamodel/src/main/kotlin/{each entities.in_namespace as entity}{path entity.namespace}/record/{entity.class_name}Record.kt.hbs"

    val addItemToContext = { model: MetamodelModel ->
        mapOf("entity" to model.entities.find{ it.name == "item" })
    }

    val addCompositeToContext = { model: MetamodelModel ->
        mapOf("entity" to model.entities.find{ it.name == "composite" })
    }

    val namespace = "laplacian.metamodel"

    val assertionForModelWithInheritedRelationship = MetamodelTemplateAssertion().withModelText(
    """
    |project_name: test
    |namespace: $namespace
    |value_domain_types: []
    |entities:
    |  - name: composite
    |    namespace: $namespace
    |    properties:
    |      - name: name
    |        type: string
    |    relationships:
    |      - name: items
    |        reference_entity_name: item
    |        cardinality: 1..N
    |        aggregate: true
    |
    |  - name: item
    |    namespace: $namespace
    |    properties:
    |      - name: name
    |        type: string
    |    relationships:
    |      - name: composite
    |        reference_entity_name: composite
    |        cardinality: '0..1'
    |        inherited: true
    |
    |      - name: children
    |        description: 子要素
    |        reference_entity_name: item
    |        aggregate: true
    |        cardinality: 'N'
    |
    |      - name: parent
    |        description: 親要素(トップレベル要素の場合はnull)
    |        reference_entity_name: item
    |        cardinality: '0..1'
    |        inherited: true
    |
    """.trimMargin()).withTemplate(recordClassTemplate)

    @Test
    fun an_aggregator_shares_its_reference_if_aggragated_objects_need_it() {
        val assertion = assertionForModelWithInheritedRelationship
        assertion.assertContains("""
        |override val items: List<Item>
        |    = ItemRecord.from(getList("items"), _model, this)
        """.trimMargin(), addCompositeToContext)

        assertion.assertContains("""
        |override val children: List<Item>
        |    = ItemRecord.from(getList("children", emptyList()), _model, this)
        """.trimMargin(), addItemToContext)
    }



    @Test
    fun an_records_constructor_requires_references_of_the_records_it_inherits_from() {
        val assertion = assertionForModelWithInheritedRelationship
        assertion.assertContains("""
        |data class ItemRecord (
        |    private val _record: Record,
        |    private val _model: Model,
        |    override val composite: Composite? = null,
        |    override val parent: Item? = null
        |): Item, Record by _record
        """.trimMargin(), addItemToContext)

        assertion.assertContains("""
        |fun from(records: RecordList, model: Model, parent: Item? = null) = records.map {
        |    ItemRecord(it.normalizeCamelcase(), model, parent = parent)
        |}
        """.trimMargin(), addItemToContext)
        assertion.assertContains("""
        |fun from(records: RecordList, model: Model, composite: Composite? = null) = records.map {
        |    ItemRecord(it.normalizeCamelcase(), model, composite = composite)
        |}
        """.trimMargin(), addItemToContext)
    }

    val assertionForModelWithoutInheritedRelationship = MetamodelTemplateAssertion().withModelText(
    """
    |project_name: test
    |namespace: $namespace
    |value_domain_types: []
    |entities:
    |  - name: composite
    |    namespace: $namespace
    |    properties:
    |      - name: name
    |        type: string
    |    relationships:
    |      - name: items
    |        reference_entity_name: item
    |        cardinality: 1..N
    |        aggregate: true
    |
    |  - name: item
    |    namespace: $namespace
    |    properties:
    |      - name: name
    |        type: string
    |    relationships:
    |      - name: children
    |        description: 子要素
    |        reference_entity_name: item
    |        aggregate: true
    |        cardinality: 'N'
    |
    |      - name: parent
    |        description: 親要素(トップレベル要素の場合はnull)
    |        reference_entity_name: item
    |        cardinality: '0..1'
    |        inherited: true
    |
    """.trimMargin()).withTemplate(recordClassTemplate)

    @Test
    fun an_aggregator_does_not_pass_its_reference_to_children_if_they_do_not_have_inherited_relationship() {
        val assertion = assertionForModelWithoutInheritedRelationship
        assertion.assertContains("""
        |override val items: List<Item>
        |    = ItemRecord.from(getList("items"), _model)
        """.trimMargin(), addCompositeToContext)

        assertion.assertContains("""
        |override val children: List<Item>
        |    = ItemRecord.from(getList("children", emptyList()), _model, this)
        """.trimMargin(), addItemToContext)

        assertion.assertContains("""
        |data class ItemRecord (
        |    private val _record: Record,
        |    private val _model: Model,
        |    override val parent: Item? = null
        |): Item, Record by _record
        """.trimMargin(), addItemToContext)

        assertion.assertContains("""
        |fun from(records: RecordList, model: Model, parent: Item? = null) = records.map {
        |    ItemRecord(it.normalizeCamelcase(), model, parent = parent)
        |}
        """.trimMargin(), addItemToContext)
    }
}




