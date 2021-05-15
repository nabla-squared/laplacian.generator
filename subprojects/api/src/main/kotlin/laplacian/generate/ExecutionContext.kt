package laplacian.generate

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Helper
import laplacian.generate.util.CsvLoader
import laplacian.handlebars.HandlebarsExtension
import laplacian.handlebars.TemplateWrapper
import laplacian.generate.util.YamlLoader
import laplacian.generate.util.mergeObjectGraph
import org.yaml.snakeyaml.Yaml
import java.io.File

class ExecutionContext {
    private var entries: Map<String, Any?> = emptyMap()
    private var modelEntryResolvers: List<ModelEntryResolver> = mutableListOf()
    private var modelSchema: File? = null
    private val handlebarsExtensions: MutableList<HandlebarsExtension> = mutableListOf()

    lateinit var baseModel: Context

    fun addHandlebarsExtension(extension: HandlebarsExtension): ExecutionContext {
        handlebarsExtensions.add(extension)
        return this
    }

    val handlebarsHelpers: Map<String, Helper<*>>
        get() = handlebarsExtensions.fold(mutableMapOf()) { acc, extension ->
            acc.putAll(extension.handlebarHelpers())
            acc
        }

    fun addModelEntryResolver(vararg resolvers: ModelEntryResolver): ExecutionContext {
        modelEntryResolvers = (modelEntryResolvers + resolvers).distinct()
        return this
    }

    fun addModel(vararg modelFiles: File): ExecutionContext {
        entries = YamlLoader.readObjects(modelFiles.toList(), modelSchema, baseModel = entries)
        entries = CsvLoader.readObjects(modelFiles.toList(), modelSchema, baseModel = entries)
        return this
    }

    fun addModel(vararg modelJson: String): ExecutionContext {
        val models = modelJson.map { json -> Yaml().load<Map<String, Any?>>(json) }
        return addModel(*models.toTypedArray())
    }

    fun addModel(vararg models: Map<String, Any?>): ExecutionContext {
        entries = models.fold(entries) { acc, model ->
            try {
                mergeObjectGraph(acc, model) as Map<String, Any?>
            }
            catch (e: RuntimeException) {
                throw IllegalStateException(
                    "A problem occurred while merging the model: $model", e
                )
            }
        }
        return this
    }

    fun setModelSchema(schemaFile: File): ExecutionContext {
        modelSchema = schemaFile
        return this
    }

    fun build(): ExecutionContext {
        baseModel = TemplateWrapper.createContext(entries, *modelEntryResolvers.map { resolver ->
            ModelEntryResolverBridge(resolver, this)
        }.toTypedArray())
        return this
    }

    private var _currentModel: Context? = null
    var currentModel: Context
        get() = _currentModel ?: baseModel
        set(model) {
            _currentModel = model
        }
}
