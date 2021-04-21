package laplacian.plugin

import com.github.jknack.handlebars.Helper
import laplacian.handlebars.HandlebarsExtension
import laplacian.handlebars.Helpers
import org.pf4j.Extension

@Extension
class CoreHandlebarsExtension: HandlebarsExtension {
    override fun handlebarHelpers(): Map<String, Helper<*>> = Helpers.helpers()
}
