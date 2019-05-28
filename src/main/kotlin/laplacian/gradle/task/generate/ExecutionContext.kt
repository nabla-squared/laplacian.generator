package laplacian.gradle.task.generate

import com.github.jknack.handlebars.Context
import laplacian.handlebars.TemplateWrapper
import laplacian.util.mergeObjectGraph
import org.yaml.snakeyaml.Yaml
import java.io.File

class ExecutionContext(
    private var entries: Map<String, Any?> = emptyMap(),
    private var modelEntryResolvers: List<ModelEntryResolver> = mutableListOf()
) {
    lateinit var baseModel: Context
    lateinit var currentTemplate: File

    fun addModelEntryResolver(vararg resolvers: ModelEntryResolver): ExecutionContext {
        modelEntryResolvers = (modelEntryResolvers + resolvers).distinct()
        return this
    }

    fun addModel(vararg models: String): ExecutionContext {
        entries = models.fold(entries) { acc, model ->
            try {
                val readModel = Yaml().load<Map<String, Any?>>(model)
                mergeObjectGraph(acc, readModel) as Map<String, Any?>
            }
            catch (e: RuntimeException) {
                throw IllegalStateException(
                    "A problem occurred while merging the model: $model", e
                )
            }
        }
        return this
    }

    fun addModel(vararg modelFiles: File): ExecutionContext {
        entries = modelFiles.fold(entries) { acc, modelFile ->
            try {
                val readModel = Yaml().load<Map<String, Any?>>(modelFile.readText())
                mergeObjectGraph(acc, readModel) as Map<String, Any?>
            }
            catch (e: RuntimeException) {
                throw IllegalStateException(
                    "A problem occurred while merging the model file (${modelFile.absolutePath})", e
                )
            }
        }
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
