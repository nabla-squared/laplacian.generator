package laplacian.metamodel.model
import laplacian.util.*
/**
 * A container for records of value_domain_type
 */
class ValueDomainTypeList(
    list: List<ValueDomainType>,
    val model: Model
) : List<ValueDomainType> by list {
}