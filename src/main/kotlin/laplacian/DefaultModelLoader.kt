package laplacian

import laplacian.util.YamlLoader
import java.io.File

class DefaultModelLoader: ModelLoader {
    override fun load(files: Iterable<File>): Map<String, Any?> {
        return YamlLoader.readObjects<Map<String, Any?>>(files)
    }
}
