package laplacian.gradle.filter

import laplacian.gradle.task.generate.ExecutionContext
import java.io.OutputStream
import java.io.Reader
import java.io.File
import org.gradle.api.file.FileCopyDetails
import java.lang.IllegalStateException

class IncludesHandler: FileCopyHandler {

    companion object {
        val TEMPLATE_PATTERN = """(.+)@([-_0-9a-zA-Z]+)@([^@]*)$""".toRegex()
    }

    lateinit var originalContent: String

    lateinit var includesName: String

    override fun handle(details: FileCopyDetails, context: ExecutionContext): Boolean {
        val m = TEMPLATE_PATTERN.matchEntire(details.name)
        if (m == null) return false
        val filename = m.groupValues[1] + m.groupValues[3]
        includesName = m.groupValues[2]
        details.name = filename
        val file = File(details.path)
        if (!file.exists()) throw IllegalStateException(
            "${file.absolutePath} could not be found."
        )
        originalContent = file.readText()
        return true
    }

    override fun copy(reader: Reader, out: OutputStream) {
        val directive = "@$includesName@"
        val regex = """(?<=^|\n)(.*)$directive(.*)\n([\s\S]*)\n(.*)$directive""".toRegex()
        val m = regex.find(originalContent)
        if (m == null) throw IllegalStateException(
            """This template does not have the directive ("$directive") at which the includes are inserted."""
        )
        val startDirective = m.groupValues[1] + directive + m.groupValues[2]
        val endDirective = m.groupValues[4] + directive
        val isAlreadyInserted = m.groupValues[3]
        val isBeingInserted = reader.readText()
        out.bufferedWriter().use {
            it.write(
                originalContent.replaceRange(
                    m.range, "$startDirective\n$isAlreadyInserted\n$isBeingInserted\n$endDirective"
                )
            )
        }
    }
}
