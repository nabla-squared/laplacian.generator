package laplacian.metamodel.model
import laplacian.util.*
/**
 * value_domain
 */
interface ValueDomain {
    /**
     * 許容される値のパターン(正規表現)
     */
    val pattern: String?
    /**
     * 許容される値のリスト
     */
    val choices: List<ValueItem>
}