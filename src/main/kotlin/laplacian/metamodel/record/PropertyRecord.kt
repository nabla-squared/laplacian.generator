package laplacian.metamodel.record
import laplacian.metamodel.model.Property
import laplacian.metamodel.model.Entity
import laplacian.metamodel.model.ValueDomain
import laplacian.metamodel.model.ValueDomainType
import laplacian.util.*
/**
 * property
 */
data class PropertyRecord (
    private val _record: Record,
    private val _model: Model,
    /**
     * the entity which aggregates this property
     */
    override val entity: Entity
): Property, Record by _record {
    /**
     * The name of this property.
     */
    override val name: String
        get() = getOrThrow("name")
    /**
     * The identifier of this property.
     */
    override val identifier: String
        get() = getOrThrow("identifier") {
            name
        }
    /**
     * Defines this property is primary_key or not.
     */
    override val primaryKey: Boolean
        get() = getOrThrow("primaryKey") {
            false
        }
    /**
     * The type of this property.
     */
    override val type: String
        get() = getOrThrow("type")
    /**
     * 制約型名
     */
    override val domainTypeName: String? by _record
    /**
     * the maximum allowed size of this property
     */
    override val size: Int
        get() = getOrThrow("size") {
            when(type) {
                "string" -> 200
                else -> throw IllegalStateException(
                    "The size of $type type field can not be defined."
                )
            }
        }
    /**
     * Defines this property is optional or not.
     */
    override val optional: Boolean
        get() = getOrThrow("optional") {
            false
        }
    /**
     * The description of this property.
     */
    override val description: String
        get() = getOrThrow("description") {
            if (type == "boolean") "Defines this ${entity.name} is $name or not." else "The $name of this ${entity.name}."
        }
    /**
     * デフォルト値
     */
    override val defaultValue: String? by _record
    /**
     * The example_value of this property.
     */
    override val exampleValue: String
        get() = getOrThrow("exampleValue") {
            defaultValue ?: when(type) {
              "string" -> "\"hogehoge\""
              "number" -> "0"
              "boolean" -> "false"
              else -> "null"
            }
        }
    /**
     * The table_column_name of this property.
     */
    override val tableColumnName: String
        get() = getOrThrow("tableColumnName") {
            identifier.lowerUnderscorize()
        }
    /**
     * The snippet of this property.
     */
    override val snippet: String? by _record
    /**
     * Defines this property is multiple or not.
     */
    override val multiple: Boolean
        get() = getOrThrow("multiple") {
            false
        }
    /**
     * domain
     */
    override val domain: ValueDomain?
        = getOrNull<Record>("domain")?.let{ ValueDomainRecord(it.normalizeCamelcase(), _model) }
    /**
     * domain_type
     */
    override val domainType: ValueDomainType?
        get() = ValueDomainTypeRecord.from(_model).find {
            it.name == domainTypeName
        }
    companion object {
        /**
         * creates record list from list of map
         */
        fun from(records: RecordList, model: Model, entity: Entity) = records.map {
            PropertyRecord(it.normalizeCamelcase(), model, entity = entity)
        }
    }
}