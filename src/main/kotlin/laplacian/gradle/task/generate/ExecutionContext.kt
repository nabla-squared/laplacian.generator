package laplacian.gradle.task.generate

import com.github.jknack.handlebars.Context
import laplacian.handlebars.TemplateWrapper
import laplacian.util.YamlLoader
import java.io.File

class ExecutionContext(
    val modelFiles: MutableList<File> = mutableListOf(),
    val modelEntryResolvers: MutableList<ModelEntryResolver> = mutableListOf()
) {
    lateinit var baseModel: Context
    lateinit var currentTemplate: File

    fun build() {
        val entries = YamlLoader.readObjects<Any?>(modelFiles)
        baseModel = TemplateWrapper.createContext(entries, *modelEntryResolvers.map { resolver ->
            ModelEntryResolverBridge(resolver, this)
        }.toTypedArray())
    }

    private var _currentModel: Context? = null
    var currentModel: Context
        get() = _currentModel ?: baseModel
        set(model) {
            _currentModel = model
        }
}
