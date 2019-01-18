package laplacian.metamodel.record
import laplacian.metamodel.model.NamedParam
import laplacian.util.*
/**
 * named_param
 */
data class NamedParamRecord (
    private val _record: Record,
    private val _model: Model
): NamedParam, Record by _record {
    /**
     * The name of this named_param.
     */
    override val name: String
        get() = getOrThrow("name")
    /**
     * The type of this named_param.
     */
    override val type: String
        get() = getOrThrow("type")
    /**
     * The description of this named_param.
     */
    override val description: String
        get() = getOrThrow("description") {
            name
        }
    companion object {
        /**
         * creates record list from list of map
         */
        fun from(records: RecordList, model: Model) = records.map {
            NamedParamRecord(it.normalizeCamelcase(), model)
        }
    }
}