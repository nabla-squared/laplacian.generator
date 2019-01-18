package laplacian.metamodel.record
import laplacian.metamodel.model.Relationship
import laplacian.metamodel.model.Entity
import laplacian.metamodel.model.PropertyMapping
import laplacian.util.*
/**
 * relationship
 */
data class RelationshipRecord (
    private val _record: Record,
    private val _model: Model,
    /**
     * the entity which aggregates this relationship
     */
    override val entity: Entity
): Relationship, Record by _record {
    /**
     * The name of this relationship.
     */
    override val name: String
        get() = getOrThrow("name")
    /**
     * The identifier of this relationship.
     */
    override val identifier: String
        get() = getOrThrow("identifier") {
            name.lowerUnderscorize()
        }
    /**
     * The cardinality of this relationship.
     */
    override val cardinality: String
        get() = getOrThrow("cardinality")
    /**
     * The reference_entity_name of this relationship.
     */
    override val referenceEntityName: String
        get() = getOrThrow("referenceEntityName")
    /**
     * Defines this relationship is aggregate or not.
     */
    override val aggregate: Boolean
        get() = getOrThrow("aggregate") {
            false
        }
    /**
     * Defines this relationship is inherited or not.
     */
    override val inherited: Boolean
        get() = getOrThrow("inherited") {
            false
        }
    /**
     * The description of this relationship.
     */
    override val description: String
        get() = getOrThrow("description") {
            name
        }
    /**
     * The snippet of this relationship.
     */
    override val snippet: String? by _record
    /**
     * mappings
     */
    override val mappings: List<PropertyMapping>
        = PropertyMappingRecord.from(getList("mappings", emptyList()), _model, this)
    /**
     * reference_entity
     */
    override val referenceEntity: Entity
        get() = EntityRecord.from(_model).find {
            it.name == referenceEntityName
        } ?: throw IllegalStateException(
            "There is no entity which meets the following condition(s): "
            + "Relationship.reference_entity_name == entity.name (=$referenceEntityName) "
            + "Possible values are: " + EntityRecord.from(_model).map {
              "(${ it.name })"
            }.joinToString()
        )
    companion object {
        /**
         * creates record list from list of map
         */
        fun from(records: RecordList, model: Model, entity: Entity) = records.map {
            RelationshipRecord(it.normalizeCamelcase(), model, entity = entity)
        }
    }
}