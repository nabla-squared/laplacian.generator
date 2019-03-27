package laplacian.gradle.filter

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.*
import java.nio.CharBuffer

class PlantUmlFilter(val reader :Reader): FilterReader(reader) {

    val filtered: Reader by lazy {
        val plantUml = reader.readText()
        val buffer = ByteArrayOutputStream()
        SourceStringReader(plantUml).outputImage(buffer, FileFormatOption(FileFormat.SVG))
        InputStreamReader(ByteArrayInputStream(buffer.toByteArray()))
    }

    override fun skip(n: Long): Long = filtered.skip(n)

    override fun ready(): Boolean = filtered.ready()

    override fun reset() = filtered.reset()

    override fun close() = filtered.close()

    override fun markSupported(): Boolean = filtered.markSupported()

    override fun mark(readAheadLimit: Int) = filtered.mark(readAheadLimit)

    override fun read(): Int = filtered.read()

    override fun read(cbuf: CharArray?, off: Int, len: Int): Int = filtered.read(cbuf, off, len)

    override fun read(target: CharBuffer?): Int = filtered.read(target)

    override fun read(cbuf: CharArray?): Int = filtered.read(cbuf)
}
