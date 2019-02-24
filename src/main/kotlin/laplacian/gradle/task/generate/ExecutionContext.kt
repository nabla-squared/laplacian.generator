package laplacian.gradle.task.generate

import com.github.jknack.handlebars.Context
import laplacian.util.TemplateWrapper

class ExecutionContext {
    var baseModel = TemplateWrapper.createContext(emptyMap<String, Any?>())
    private var _currentModel: Context? = null
    var currentModel: Context
        get() = _currentModel ?: baseModel
        set(model) {
            _currentModel = model
        }
}
