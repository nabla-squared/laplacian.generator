package laplacian.metamodel.model
import laplacian.util.*
/**
 * 各エンティティに対するルートクエリ
 */
interface Query {
    /**
     * クエリ名称
     */
    val name: String
    /**
     * 識別子
     */
    val identifier: String
    /**
     * 結果型
     */
    val type: String
    /**
     * クエリ結果エンティティ名
     */
    val resultEntityName: String?
    /**
     * 詳細
     */
    val description: String
    /**
     * 多重度
     */
    val cardinality: String
    /**
     * クエリスクリプト
     */
    val snippet: String
    /**
     * Defines this query is oneliner or not.
     */
    val oneliner: Boolean
        get() = !snippet.contains("""\breturn\b""".toRegex())
    /**
     * エンティティ
     */
    val entity: Entity
    /**
     * クエリ結果エンティティ
     */
    val resultEntity: Entity?
}