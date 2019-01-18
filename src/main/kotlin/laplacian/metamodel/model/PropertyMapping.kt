package laplacian.metamodel.model
import laplacian.util.*
/**
 * property_mapping
 */
interface PropertyMapping {
    /**
     * The from of this property_mapping.
     */
    val from: String
    /**
     * The to of this property_mapping.
     */
    val to: String
    /**
     * The null_value of this property_mapping.
     */
    val nullValue: String?
    /**
     * relationship
     */
    val relationship: Relationship
    /**
     * property
     */
    val property: Property
        get() = relationship.entity.properties.find{ it.name == from }!!
    /**
     * reference_property
     */
    val referenceProperty: Property
        get() = relationship.referenceEntity.properties.find{ it.name == to }!!
}