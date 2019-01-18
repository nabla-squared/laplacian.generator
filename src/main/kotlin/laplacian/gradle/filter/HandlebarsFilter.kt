package laplacian.gradle.filter

import laplacian.gradle.task.ProjectTemplate
import laplacian.util.handlebars
import laplacian.util.stripBlankLines
import java.io.FilterReader
import java.io.Reader
import java.io.StringReader
import java.nio.CharBuffer

class HandlebarsFilter(val reader :Reader): FilterReader(reader) {

    val template = reader.readText()

    lateinit var expanded: Reader

    fun setExecutionContext(exec: ProjectTemplate.ExecutionContext) {
        val model = exec.currentModel
        expanded = StringReader(template.handlebars().apply(model).stripBlankLines())
    }

    override fun skip(n: Long): Long = expanded.skip(n)

    override fun ready(): Boolean = expanded.ready()

    override fun reset() = expanded.reset()

    override fun close() = expanded.close()

    override fun markSupported(): Boolean = expanded.markSupported()

    override fun mark(readAheadLimit: Int) = expanded.mark(readAheadLimit)

    override fun read(): Int = expanded.read()

    override fun read(cbuf: CharArray?, off: Int, len: Int): Int = expanded.read(cbuf, off, len)

    override fun read(target: CharBuffer?): Int = expanded.read(target)

    override fun read(cbuf: CharArray?): Int = expanded.read(cbuf)
}
