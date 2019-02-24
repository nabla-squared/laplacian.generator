package laplacian

import java.io.File

interface ModelLoader {
    fun load(files: Iterable<File>): Map<String, Any?>
}
