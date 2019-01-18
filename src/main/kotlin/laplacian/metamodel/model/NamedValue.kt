package laplacian.metamodel.model
import laplacian.util.*
/**
 * named_value
 */
interface NamedValue {
    /**
     * The name of this named_value.
     */
    val name: String
    /**
     * The expression of this named_value.
     */
    val expression: String
}