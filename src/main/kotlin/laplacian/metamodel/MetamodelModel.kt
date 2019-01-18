package laplacian.metamodel
import laplacian.util.*
import laplacian.metamodel.model.EntityList
import laplacian.metamodel.record.EntityRecord
import laplacian.util.*
import laplacian.metamodel.model.ValueDomainTypeList
import laplacian.metamodel.record.ValueDomainTypeRecord
class MetamodelModel(model: Model): Map<String, Any?> by model {
    val entities: EntityList = EntityRecord.from(model)
    val value_domain_types: ValueDomainTypeList = ValueDomainTypeRecord.from(model)
}