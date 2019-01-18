package laplacian.metamodel.model
import laplacian.util.*
/**
 * value_item
 */
interface ValueItem {
    /**
     * The value of this value_item.
     */
    val value: String
    /**
     * The label of this value_item.
     */
    val label: String
    /**
     * The description of this value_item.
     */
    val description: String
}