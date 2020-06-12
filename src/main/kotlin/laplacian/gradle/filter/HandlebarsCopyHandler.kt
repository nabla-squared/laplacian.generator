package laplacian.gradle.filter

import laplacian.gradle.task.generate.ExecutionContext
import java.io.OutputStream
import java.io.Reader
import java.io.StringReader
import laplacian.util.*
import org.gradle.api.file.FileCopyDetails

class HandlebarsCopyHandler() : FileCopyHandler {

    companion object {
        val TEMPLATE_PATTERN = """(.*)(?:\.hbs\.(.+)|\.(.+)\.hbs)$""".toRegex()
    }

    override fun handle(details: FileCopyDetails, context: ExecutionContext): Boolean {
        val m = TEMPLATE_PATTERN.matchEntire(details.name)
        if (m == null) return false
        val baseName = m.groups[1]!!.value
        val extension = (m.groups[2] ?: m.groups[3])!!.value
        details.name = "$baseName.$extension"
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
