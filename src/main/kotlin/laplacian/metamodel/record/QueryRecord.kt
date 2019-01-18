package laplacian.metamodel.record
import laplacian.metamodel.model.Query
import laplacian.metamodel.model.Entity
import laplacian.util.*
/**
 * 各エンティティに対するルートクエリ
 */
data class QueryRecord (
    private val _record: Record,
    private val _model: Model,
    /**
     * the entity which aggregates this query
     */
    override val entity: Entity
): Query, Record by _record {
    /**
     * クエリ名称
     */
    override val name: String
        get() = getOrThrow("name")
    /**
     * 識別子
     */
    override val identifier: String
        get() = getOrThrow("identifier") {
            name.lowerUnderscorize()
        }
    /**
     * 結果型
     */
    override val type: String
        get() = getOrThrow("type") {
            resultEntity?.className?.let { className ->
                if (cardinality.contains("*")) "List<$className>" else className
            }
        }
    /**
     * クエリ結果エンティティ名
     */
    override val resultEntityName: String? by _record
    /**
     * 詳細
     */
    override val description: String
        get() = getOrThrow("description") {
            name
        }
    /**
     * 多重度
     */
    override val cardinality: String
        get() = getOrThrow("cardinality") {
            "*"
        }
    /**
     * クエリスクリプト
     */
    override val snippet: String
        get() = getOrThrow("snippet")
    /**
     * クエリ結果エンティティ
     */
    override val resultEntity: Entity?
        get() = EntityRecord.from(_model).find {
            it.name == resultEntityName
        }
    companion object {
        /**
         * creates record list from list of map
         */
        fun from(records: RecordList, model: Model, entity: Entity) = records.map {
            QueryRecord(it.normalizeCamelcase(), model, entity = entity)
        }
    }
}