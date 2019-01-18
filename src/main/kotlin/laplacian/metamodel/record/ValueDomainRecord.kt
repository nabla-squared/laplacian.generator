package laplacian.metamodel.record
import laplacian.metamodel.model.ValueDomain
import laplacian.metamodel.model.ValueItem
import laplacian.util.*
/**
 * value_domain
 */
data class ValueDomainRecord (
    private val _record: Record,
    private val _model: Model
): ValueDomain, Record by _record {
    /**
     * 許容される値のパターン(正規表現)
     */
    override val pattern: String? by _record
    /**
     * 許容される値のリスト
     */
    override val choices: List<ValueItem>
        = ValueItemRecord.from(getList("choices", emptyList()), _model)
    companion object {
        /**
         * creates record list from list of map
         */
        fun from(records: RecordList, model: Model) = records.map {
            ValueDomainRecord(it.normalizeCamelcase(), model)
        }
    }
}