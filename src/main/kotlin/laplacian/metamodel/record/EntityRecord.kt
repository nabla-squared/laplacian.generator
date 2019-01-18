package laplacian.metamodel.record
import laplacian.metamodel.model.Entity
import laplacian.metamodel.model.EntityList
import laplacian.metamodel.model.Property
import laplacian.metamodel.model.Relationship
import laplacian.metamodel.model.Query
import laplacian.util.*
/**
 * エンティティ
 */
data class EntityRecord (
    private val _record: Record,
    private val _model: Model
): Entity, Record by _record {
    /**
     * 名称
     */
    override val name: String
        get() = getOrThrow("name")
    /**
     * 名前空間
     */
    override val namespace: String
        get() = getOrThrow("namespace") {
            if (inherited) {
                inheritedFrom.first().referenceEntity.namespace
            }
            else {
                _model.retrieve("project.namespace") ?: throw IllegalStateException(
                    "The ${name} entity does not have namespace." +
                    "You should give it or set the default(project) namespace instead."
                )
            }
        }
    /**
     * 識別子 省略時は名称を使用
     */
    override val identifier: String
        get() = getOrThrow("identifier") {
            name.lowerUnderscorize()
        }
    /**
     * 詳細
     */
    override val description: String
        get() = getOrThrow("description") {
            name
        }
    /**
     * 値オブジェクトかどうか
     */
    override val valueObject: Boolean
        get() = getOrThrow("valueObject") {
            false
        }
    /**
     * このエンティティのプロパティ
     */
    override val properties: List<Property>
        = PropertyRecord.from(getList("properties"), _model, this)
    /**
     * このエンティティと他のエンティティの関連
     */
    override val relationships: List<Relationship>
        = RelationshipRecord.from(getList("relationships", emptyList()), _model, this)
    /**
     * このエンティティに対するルートクエリ
     */
    override val queries: List<Query>
        = QueryRecord.from(getList("queries", emptyList()), _model, this)
    companion object {
        /**
         * creates record list from list of map
         */
        fun from(model: Model): EntityList {
            val entities = model.getList<Record>("entities", emptyList()).map {
                EntityRecord(it.normalizeCamelcase(), model)
            }
            return EntityList(entities, model)
        }
    }
}