package laplacian.metamodel.model
import laplacian.util.*
/**
 * named_param
 */
interface NamedParam {
    /**
     * The name of this named_param.
     */
    val name: String
    /**
     * The type of this named_param.
     */
    val type: String
    /**
     * The description of this named_param.
     */
    val description: String
}