package laplacian.handlebars

import com.github.jknack.handlebars.Helper
import org.pf4j.ExtensionPoint

interface HandlebarsExtension: ExtensionPoint {
    fun handlebarHelpers(): Map<String, Helper<*>>
}
