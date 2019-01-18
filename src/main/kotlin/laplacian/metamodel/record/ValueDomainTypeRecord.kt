package laplacian.metamodel.record
import laplacian.metamodel.model.ValueDomainType
import laplacian.metamodel.model.ValueDomainTypeList
import laplacian.metamodel.model.ValueDomain
import laplacian.util.*
/**
 * value_domain_type
 */
data class ValueDomainTypeRecord (
    private val _record: Record,
    private val _model: Model
): ValueDomainType, Record by _record {
    /**
     * The name of this value_domain_type.
     */
    override val name: String
        get() = getOrThrow("name")
    /**
     * The type of this value_domain_type.
     */
    override val type: String
        get() = getOrThrow("type")
    /**
     * The description of this value_domain_type.
     */
    override val description: String
        get() = getOrThrow("description") {
            name
        }
    /**
     * domain
     */
    override val domain: ValueDomain
        = ValueDomainRecord(getOrThrow<Record>("domain").normalizeCamelcase(), _model)
    companion object {
        /**
         * creates record list from list of map
         */
        fun from(model: Model): ValueDomainTypeList {
            val entities = model.getList<Record>("value_domain_types", emptyList()).map {
                ValueDomainTypeRecord(it.normalizeCamelcase(), model)
            }
            return ValueDomainTypeList(entities, model)
        }
    }
}