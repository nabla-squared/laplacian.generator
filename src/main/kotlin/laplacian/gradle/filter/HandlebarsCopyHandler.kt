package laplacian.gradle.filter

import laplacian.gradle.task.generate.ExecutionContext
import java.io.OutputStream
import java.io.Reader
import java.io.StringReader
import laplacian.util.*
import org.gradle.api.file.FileCopyDetails

class HandlebarsCopyHandler() : FileCopyHandler {

    companion object {
        val NO_EXTENSION_FILE_NAMES = listOf("Dockerfile").joinToString("|")
        val TEMPLATE_PATTERN = """(.*)(?:\.hbs\.(.+)|\.(.+)\.hbs|(${NO_EXTENSION_FILE_NAMES})\.hbs)$""".toRegex()
    }

    override fun handle(details: FileCopyDetails, context: ExecutionContext): Boolean {
        val m = TEMPLATE_PATTERN.matchEntire(details.name)
        if (m == null) return false
        val baseName = m.groupValues[1]
        val extension = m.groupValues[2].ifEmpty{ m.groupValues[3] }
        details.name = m.groupValues[4].ifEmpty{ "$baseName.$extension" }
        this.context = context
        return true
    }

    lateinit var context: ExecutionContext

    override fun copy(reader: Reader): Reader {
        val template = reader.readText()
        val baseDir = context.currentTemplate.parentFile
        return StringReader(
            template
                .handlebars(baseDir)
                .apply(context.currentModel)
                .stripBlankLines()
        )
    }
}
