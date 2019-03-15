package laplacian.util

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.scanner.ScannerException
import java.io.File

class YamlLoader {

    companion object {

        fun <T> readObjects(files: Iterable<File>): Map<String, T> {
            val parser = Yaml()
            val result: Map<String, T> = mutableMapOf()
            return files.fold(result) { acc, file ->
                mergeObjectGraph(acc, readObjects<T>(parser, file), file) as Map<String, T>
            }
        }

        private fun <T> readObjects(parser: Yaml, file: File): Map<String, T> {
            try {
                return parser.load(file.reader()) as Map<String, T>
            }
            catch (e: ScannerException) {
                throw IllegalStateException(
                    "Failed to parse a yaml file: ${file.absolutePath}", e
                )
            }
        }

        private fun mergeObjectGraph(one: Any, another: Any, file: File, path: String = ""): Any {
            if (one is Map<*, *> && another is Map<*, *>) {
                return one + another.map{ entry ->
                    val k = entry.key
                    val anotherValue = entry.value
                    val value = one[k]
                    when {
                        (value == null) -> k to anotherValue
                        (anotherValue == null) -> k to value
                        else -> k to mergeObjectGraph(
                            value,
                            anotherValue,
                            file,
                            path + (if (path.isEmpty()) "" else ".") + k
                        )
                    }
                }
            }
            if (one is List<*> && another is List<*>) {
                return one + another
            }
            else {
                throw java.lang.IllegalStateException(
                    "While merging the model file (${file.absolutePath})," +
                    " the following model items at '${if (path.isEmpty()) "root" else path}' conflict" +
                    ": $one and $another"
                )
            }
        }

    }
}
