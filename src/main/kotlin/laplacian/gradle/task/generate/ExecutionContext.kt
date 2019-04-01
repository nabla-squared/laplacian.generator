package laplacian.gradle.task.generate

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.ValueResolver
import com.github.jknack.handlebars.context.MapValueResolver
import laplacian.util.RecordList
import laplacian.util.TemplateWrapper
import laplacian.util.YamlLoader
import org.gradle.api.file.FileCopyDetails
import java.io.File

class ExecutionContext(
    val modelFiles: MutableList<File> = mutableListOf(),
    val modelEntryResolvers: MutableList<ModelEntryResolver> = mutableListOf()
) {
    lateinit var baseModel: Context
    lateinit var fileCopyDetails: FileCopyDetails

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
