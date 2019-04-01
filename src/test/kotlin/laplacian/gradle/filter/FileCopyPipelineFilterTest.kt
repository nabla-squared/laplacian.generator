package laplacian.gradle.filter

import laplacian.gradle.task.generate.ExecutionContext
import org.gradle.api.file.FileCopyDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.OutputStream
import java.io.Reader
import java.io.StringReader

class FileCopyPipelineFilterTest {

    val duplicate = object : FileCopyHandler {
        override fun handle(details: FileCopyDetails) = true
        override fun copy(reader: Reader, out: OutputStream) {
            out.bufferedWriter().use {
                val content = reader.readText()
                it.write(content + content)
            }
        }
    }

    val capitalize = object : FileCopyHandler {
        override fun handle(details: FileCopyDetails) = true
        override fun copy(reader: Reader, out: OutputStream) {
            out.bufferedWriter().use {
                val content = reader.readText().toUpperCase()
                it.write(content)
            }
        }
    }

    @Test
    fun converts_single_file() {
        val reader = StringReader("hoge")
        val chain = FileCopyPipelineFilter(reader).apply {
            fileCopyPipeline = listOf(duplicate)
        }
        assertEquals(
            "hogehoge", chain.readText()
        )
    }

    @Test
    fun conbines_multiple_converters_in_a_filter() {
        val reader = StringReader("hoge")
        val chain = FileCopyPipelineFilter(reader).apply {
            fileCopyPipeline = listOf(duplicate, capitalize)
        }
        assertEquals(
            "HOGEHOGE", chain.readText()
        )
    }
}
