package laplacian.metamodel.record
import laplacian.metamodel.model.PropertyMapping
import laplacian.metamodel.model.Relationship
import laplacian.metamodel.model.Property
import laplacian.util.*
/**
 * property_mapping
 */
data class PropertyMappingRecord (
    private val _record: Record,
    private val _model: Model,
    /**
     * the relationship which aggregates this property_mapping
     */
    override val relationship: Relationship
): PropertyMapping, Record by _record {
    /**
     * The from of this property_mapping.
     */
    override val from: String
        get() = getOrThrow("from")
    /**
     * The to of this property_mapping.
     */
    override val to: String
        get() = getOrThrow("to")
    /**
     * The null_value of this property_mapping.
     */
    override val nullValue: String? by _record
    companion object {
        /**
         * creates record list from list of map
         */
        fun from(records: RecordList, model: Model, relationship: Relationship) = records.map {
            PropertyMappingRecord(it.normalizeCamelcase(), model, relationship = relationship)
        }
    }
}