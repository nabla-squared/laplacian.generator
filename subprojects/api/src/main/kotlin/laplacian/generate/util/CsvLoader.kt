package laplacian.generate.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException

class CsvLoader {
    companion object {
        private val SUPPORTED_FILE_EXTENSIONS = listOf("csv")

        fun readObjects(files: Iterable<File>, schemaFile: File? = null, baseModel: Map<String, Any?> = emptyMap()): Map<String, Any?> {
            val schema = schemaFile?.let { readJsonSchema(it) }
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

        private fun readJsonSchema(file: File): JsonSchema {
            val factory = JsonSchemaFactory.builder(jsonSchemaFactory).objectMapper(mapper).build()
            val node = mapper.readTree(file)
            return factory.getSchema(node)
        }

        private val jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
        private val mapper = ObjectMapper().registerKotlinModule().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        private val csvMapper = CsvMapper().registerModule(KotlinModule())
        private const val PREFIX_SEPARATOR = "__"

        private fun readObjects(file: File, schema: JsonSchema?): Map<String, Any?> {
            val key = file.nameWithoutExtension.let {
                if (it.contains(PREFIX_SEPARATOR)) it.substringAfterLast(PREFIX_SEPARATOR) else it
            }
            try {
                lateinit var headers: CsvSchema
                val records: List<Map<String, String>> = file.bufferedReader().use { reader ->
                    headers = reader.readLine().split(",").fold(CsvSchema.builder()) { headers, header ->
                        val trimmedHeader = header.trim().trim('"')
                        val typeAnnotation = trimmedHeader.substringAfter("@", "string")
                        val columnName = trimmedHeader.substringBefore("@")
                        val columnType = when (typeAnnotation) {
                            "number" -> CsvSchema.ColumnType.NUMBER
                            "boolean" -> CsvSchema.ColumnType.BOOLEAN
                            "string" -> CsvSchema.ColumnType.STRING
                            "array" -> CsvSchema.ColumnType.NUMBER_OR_STRING
                            else -> throw IllegalArgumentException("Unknown type annotation in: $header.")
                        }
                        headers.setArrayElementSeparator(",")
                        headers.addColumn(columnName, columnType)
                    }.build()
                    csvMapper
                        .readerForMapOf(String::class.java)
                        .with(headers)
                        .readValues<Map<String, String>>(reader.readText())
                        .readAll()
                }
                if (schema == null) return mapOf(key to records)

                /*
                val columnSchema = schema.schemaNode["properties"]?.get(key)?.get("items")?.get("properties")
                    ?: throw IllegalArgumentException(
                        """The given model is not described in the JSON schema: {"properties":{"$key":{"items":[{"properties":{****}]}}}"""
                    )
                val columnTypes = mutableMapOf<String, String>()
                columnSchema.fields().forEach { (columnName, column) ->
                    columnTypes[columnName] = column["type"].asText()
                }
                 */
                //NOTE: Manually mangle the read records here as Jackson CSV currently does not adhere the
                //      column types while reading.
                val typedRecords = records.map { record ->
                    record
                        .filter { (_, value) -> value.isNotEmpty() }
                        .map { (columnName, value) ->
                        columnName to
                                when (headers.column(columnName).type) {
                                    CsvSchema.ColumnType.NUMBER -> if (value.contains(".")) value.toDouble() else value.toInt()
                                    CsvSchema.ColumnType.BOOLEAN -> value.toBoolean()
                                    CsvSchema.ColumnType.NUMBER_OR_STRING -> mapper.readValue(value, List::class.java)
                                else -> value
                        }
                    }.toMap()
                }
                val resultModel = mapOf(key to typedRecords)
                val node = mapper.convertValue<JsonNode>(resultModel)
                val validationErrors = schema.validate(node)
                if (validationErrors.isNotEmpty()) throw JsonSchemaValidationError(validationErrors)
                return resultModel
            }
            catch (e: Exception) {
                throw IllegalStateException(
                    "Failed to parse the following csv file on ${file.absolutePath}.", e
                )
            }

        }
    }
}
