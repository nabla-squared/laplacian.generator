package laplacian.metamodel.record
import laplacian.metamodel.model.NamedValue
import laplacian.util.*
/**
 * named_value
 */
data class NamedValueRecord (
    private val _record: Record,
    private val _model: Model
): NamedValue, Record by _record {
    /**
     * The name of this named_value.
     */
    override val name: String
        get() = getOrThrow("name")
    /**
     * The expression of this named_value.
     */
    override val expression: String
        get() = getOrThrow("expression")
    companion object {
        /**
         * creates record list from list of map
         */
        fun from(records: RecordList, model: Model) = records.map {
            NamedValueRecord(it.normalizeCamelcase(), model)
        }
    }
}