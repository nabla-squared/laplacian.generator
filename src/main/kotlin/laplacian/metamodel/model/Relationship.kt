package laplacian.metamodel.model
import laplacian.util.*
/**
 * relationship
 */
interface Relationship {
    /**
     * The name of this relationship.
     */
    val name: String
    /**
     * The identifier of this relationship.
     */
    val identifier: String
    /**
     * The cardinality of this relationship.
     */
    val cardinality: String
    /**
     * The reference_entity_name of this relationship.
     */
    val referenceEntityName: String
    /**
     * Defines this relationship is aggregate or not.
     */
    val aggregate: Boolean
    /**
     * Defines this relationship is inherited or not.
     */
    val inherited: Boolean
    /**
     * The description of this relationship.
     */
    val description: String
    /**
     * The snippet of this relationship.
     */
    val snippet: String?
    /**
     * The class_name of this relationship.
     */
    val className: String
        get() = if (multiple) "List<${referenceEntity.className}>" else (referenceEntity.className + if (nullable) "?" else "")
    /**
     * Defines this relationship is multiple or not.
     */
    val multiple: Boolean
        get() = cardinality.contains("""(\*|N|\.\.[2-9][0-9]+)""".toRegex())
    /**
     * Defines this relationship is allows_empty or not.
     */
    val allowsEmpty: Boolean
        get() = cardinality == "N" || cardinality == "*" || cardinality.contains("""(0\.\.)""".toRegex())
    /**
     * Defines this relationship is nullable or not.
     */
    val nullable: Boolean
        get() = !multiple && allowsEmpty
    /**
     * The property_name of this relationship.
     */
    val propertyName: String
        get() = identifier.lowerCamelize()
    /**
     * Defines this relationship is bidirectional or not.
     */
    val bidirectional: Boolean
        get() = aggregate && referenceEntity.relationships.any {
            it.inherited && (it.referenceEntity.fqn == this.entity.fqn)
        }
    /**
     * Defines this relationship is recursive or not.
     */
    val recursive: Boolean
        get() = aggregate && (referenceEntity.fqn == entity.fqn)
    /**
     * entity
     */
    val entity: Entity
    /**
     * mappings
     */
    val mappings: List<PropertyMapping>
    /**
     * reference_entity
     */
    val referenceEntity: Entity
}