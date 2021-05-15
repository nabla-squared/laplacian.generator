package laplacian.generate.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.lang.Exception


class YamlLoader {

    companion object {

        private val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        private val jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
        private val SUPPORTED_FILE_EXTENSIONS = listOf("yaml", "yml", "json", "js")

        private fun readJsonSchema(file: File): JsonSchema {
            val factory = JsonSchemaFactory.builder(jsonSchemaFactory).objectMapper(mapper).build()
            val node = mapper.readTree(file)
            return factory.getSchema(node)
        }

        fun readObjects(files: Iterable<File>, schemaFile: File? = null, baseModel: Map<String, Any?> = emptyMap()): Map<String, Any?> {
            val schema: JsonSchema? = schemaFile?.let { readJsonSchema(it) }
            return files.filter{ SUPPORTED_FILE_EXTENSIONS.contains(it.extension) }.fold(baseModel) { acc, file ->
                try {
                    mergeObjectGraph(acc, readObjects(file, schema)) as Map<String, Any?>
                }
                catch (e: RuntimeException) {
                   throw IllegalStateException(
                      "While merging the model file (${file.absolutePath})", e
                   )
                }
            }
        }

        private fun readObjects(file: File, schema: JsonSchema?): Map<String, Any?> {
            val yaml = file.readText()
            if (yaml.isBlank()) return emptyMap()
            try {
                val readModel = Yaml().load<Map<String, Any?>>(yaml)
                if (schema != null) {
                    val node = mapper.convertValue<JsonNode>(readModel)
                    val validationErrors = schema.validate(node)
                    if (validationErrors.isNotEmpty()) throw JsonSchemaValidationError(validationErrors)
                }
                return readModel
            }
            catch (e: Exception) {
                throw IllegalStateException(
                    "Failed to parse the following yaml file on ${file.absolutePath}.", e
                )
            }
        }
    }
}
