package laplacian.gradle.filter

import java.io.*
import java.nio.CharBuffer

class FileCopyPipelineFilter(
    val reader: Reader
) : FilterReader(reader) {

    var fileCopyPipeline: List<FileCopyHandler> = mutableListOf()

    private val converted: Reader by lazy {
        fileCopyPipeline.fold(reader) { reader, converter ->
            converter.copy(reader)
        }
    }

    override fun skip(n: Long): Long = converted.skip(n)

    override fun ready(): Boolean = converted.ready()

    override fun reset() = converted.reset()

    override fun close() = converted.close()

    override fun markSupported(): Boolean = converted.markSupported()

    override fun mark(readAheadLimit: Int) = converted.mark(readAheadLimit)

    override fun read(): Int = converted.read()

    override fun read(cbuf: CharArray?, off: Int, len: Int): Int = converted.read(cbuf, off, len)

    override fun read(target: CharBuffer?): Int = converted.read(target)

    override fun read(cbuf: CharArray?): Int = converted.read(cbuf)
}
