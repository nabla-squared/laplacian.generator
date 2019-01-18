package laplacian.metamodel.model
import laplacian.util.*
/**
 * value_domain_type
 */
interface ValueDomainType {
    /**
     * The name of this value_domain_type.
     */
    val name: String
    /**
     * The type of this value_domain_type.
     */
    val type: String
    /**
     * The description of this value_domain_type.
     */
    val description: String
    /**
     * domain
     */
    val domain: ValueDomain
}