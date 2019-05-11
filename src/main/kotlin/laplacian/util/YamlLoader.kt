package laplacian.util

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.scanner.ScannerException
import java.io.File
import java.lang.RuntimeException

class YamlLoader {

    companion object {

        fun <T> readObjects(files: Iterable<File>): Map<String, T> {
            val parser = Yaml()
            val result: Map<String, T> = mutableMapOf()
            return files.fold(result) { acc, file ->
                try {
                    mergeObjectGraph(acc, readObjects<T>(parser, file)) as Map<String, T>
                }
                catch (e: RuntimeException) {
                   throw IllegalStateException(
                      "While merging the model file (${file.absolutePath})", e
                   )
                }
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



    }
}
