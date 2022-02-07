package laplacian.generate.copyhandler

import com.github.jknack.handlebars.Template
import laplacian.generate.util.handlebars
import laplacian.generate.util.stripBlankLines
import java.io.File

class HandlebarsCopyHandler() : FileCopyHandler {

    companion object {
        val NO_EXTENSION_FILE_NAMES = listOf("Dockerfile").joinToString("|")
        val TEMPLATE_PATTERN = """(.*)(?:\.hbs\.(.+)|\.(.+)\.hbs|(${NO_EXTENSION_FILE_NAMES})\.hbs)$""".toRegex()
        val COMPILED_TEMPLATE_CACHE = hashMapOf<File, Template>()
    }

    override fun handle(details: FileCopyDetails): Boolean {
        val m = TEMPLATE_PATTERN.matchEntire(details.destFileName) ?: return true
        val baseName = m.groupValues[1]
        val extension = m.groupValues[2].ifEmpty{ m.groupValues[3] }
        details.destFileName = m.groupValues[4].ifEmpty{ "$baseName.$extension" }
        val template = COMPILED_TEMPLATE_CACHE.getOrPut(details.templateFile) {
            details.template.handlebars(details.context.handlebarsHelpers, details.templateFile.parentFile)
        }
        details.content = template.apply(details.context.currentModel).stripBlankLines()
        return true
    }
}
