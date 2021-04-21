package laplacian.generate.copyhandler

import laplacian.generate.util.handlebars
import laplacian.generate.util.stripBlankLines

class HandlebarsCopyHandler() : FileCopyHandler {

    companion object {
        val NO_EXTENSION_FILE_NAMES = listOf("Dockerfile").joinToString("|")
        val TEMPLATE_PATTERN = """(.*)(?:\.hbs\.(.+)|\.(.+)\.hbs|(${NO_EXTENSION_FILE_NAMES})\.hbs)$""".toRegex()
    }

    override fun handle(details: FileCopyDetails): Boolean {
        val m = TEMPLATE_PATTERN.matchEntire(details.destFileName)
        if (m == null) return true
        val baseName = m.groupValues[1]
        val extension = m.groupValues[2].ifEmpty{ m.groupValues[3] }
        details.destFileName = m.groupValues[4].ifEmpty{ "$baseName.$extension" }
        details.content = details.template
            .handlebars(
                details.context.handlebarsHelpers,
                details.templateFile.parentFile,
            )
            .apply(details.context.currentModel)
            .stripBlankLines()
        return true
    }
}
