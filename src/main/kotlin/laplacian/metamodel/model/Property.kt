package laplacian.metamodel.model
import laplacian.util.*
/**
 * property
 */
interface Property {
    /**
     * The name of this property.
     */
    val name: String
    /**
     * The identifier of this property.
     */
    val identifier: String
    /**
     * Defines this property is primary_key or not.
     */
    val primaryKey: Boolean
    /**
     * The type of this property.
     */
    val type: String
    /**
     * 制約型名
     */
    val domainTypeName: String?
    /**
     * the maximum allowed size of this property
     */
    val size: Int
    /**
     * Defines this property is optional or not.
     */
    val optional: Boolean
    /**
     * The description of this property.
     */
    val description: String
    /**
     * デフォルト値
     */
    val defaultValue: String?
    /**
     * The example_value of this property.
     */
    val exampleValue: String
    /**
     * The table_column_name of this property.
     */
    val tableColumnName: String
    /**
     * The snippet of this property.
     */
    val snippet: String?
    /**
     * Defines this property is multiple or not.
     */
    val multiple: Boolean
    /**
     * The property_name of this property.
     */
    val propertyName: String
        get() = identifier.lowerCamelize()
    /**
     * The class_name of this property.
     */
    val className: String
        get() = (if (type == "number") "Int" else type.upperCamelize()).let {
            if (multiple) "List<$it>" else it
        }
    /**
     * Null値が許容されるかどうか
     */
    val nullable: Boolean
        get() = optional && (defaultValue == null)
    /**
     * entity
     */
    val entity: Entity
    /**
     * domain
     */
    val domain: ValueDomain?
    /**
     * domain_type
     */
    val domainType: ValueDomainType?
}