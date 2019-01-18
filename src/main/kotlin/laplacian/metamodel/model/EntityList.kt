package laplacian.metamodel.model
import laplacian.util.*
/**
 * A container for records of entity
 */
class EntityList(
    list: List<Entity>,
    val model: Model
) : List<Entity> by list {
    /**
     * トップレベルエンティティの一覧
     */
    val topLevel: List<Entity>
        get() {
            return filter{ it.topLevel }
        }
    /**
     * The top level entities which are included in the same namespace.
     */
    val topLevelInNamespace: List<Entity>
        get() {
            return inNamespace.filter{ it.topLevel }
        }
    val inNamespace: List<Entity>
        get() = filter {
            it.namespace.startsWith(model.retrieve<String>("project.namespace")!!)
        }
}