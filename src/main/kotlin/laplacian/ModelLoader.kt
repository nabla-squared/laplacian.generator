package laplacian

import java.io.File

interface ModelLoader<T:Map<String, Any?>> {
    fun load(files: Iterable<File>): T
}
