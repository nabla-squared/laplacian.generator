package laplacian.metamodel
import laplacian.util.*
import laplacian.ModelLoader
import java.io.File
/**
 * A loader class for MetamodelModel.
 */
class MetamodelModelLoader : ModelLoader<MetamodelModel> {
    /**
     * load a model object from the given yaml files.
     */
    override fun load(files: Iterable<File>): MetamodelModel {
        val model: Model = YamlLoader.readObjects(files)
        return MetamodelModel(model)
    }
}