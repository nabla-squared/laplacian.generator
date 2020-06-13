package laplacian.gradle.filter

import laplacian.gradle.task.generate.ExecutionContext
import java.io.*
import org.gradle.api.file.FileCopyDetails
import java.lang.IllegalStateException

class IncludesHandler: FileCopyHandler {

    companion object {
        val TEMPLATE_PATTERN = """(.+)@([-_0-9a-zA-Z]+)@([^@]*)$""".toRegex()
    }

    lateinit var context: ExecutionContext
    var includesName: String? = null


    private val includes: MutableMap<String, MutableMap<String, (Reader) -> Reader>> =
        mutableMapOf()

    private fun lookupIncludes(path: String) : MutableMap<String, (Reader) -> Reader> =
        includes.getOrPut(path){ mutableMapOf() }

    private fun registerIncludes(path: String, key: String, include: (Reader) -> Reader) =
        includes.getOrPut(path){ mutableMapOf() }.put(key, include)

    override fun handle(details: FileCopyDetails, context: ExecutionContext): Boolean {
        this.context = context
        val m = TEMPLATE_PATTERN.matchEntire(details.name)
        if (m != null) {
            val filename = m.groupValues[1] + m.groupValues[3]
            includesName = m.groupValues[2]
            details.name = filename
        }
        else {
            includesName = null
        }
        return true
    }

    private fun registerIncludes(includeReader: Reader): Reader {
        val directive = "@$includesName@"
        val included = includeReader.readText()
        val path = context.currentTarget.path
        val include = { reader: Reader ->
            val regex = """(?<=^|\n)(.*)$directive(.*)(?=\n)([\s\S]*)\n(.*)$directive""".toRegex()
            val originalContent = reader.readText()
            val m = regex.find(originalContent)
            val content = if (m == null) {
                originalContent
            } else {
                val alreadyInserted = m.groupValues[3]
                val m2 = regex.find(included)
                originalContent.replaceRange(m.range, if (m2 == null) {
                    included
                } else {
                    included.replaceRange(
                        m2.range,
                        m2.groupValues[1] + alreadyInserted + m2.groupValues[2]
                    )
                })
            }
            StringReader(content)
        }
        registerIncludes(path, includesName!!, include)
        return if (context.currentContent != null) {
            include(StringReader(context.currentContent))
        }
        else {
            includeReader
        }
    }

    private fun applyIncludes(reader: Reader): Reader {
        val path = context.currentTarget.path
        val includes = lookupIncludes(path)
        return if (includes.isEmpty()) {
            reader
        }
        else {
            includes.values.fold(reader){ r, inc -> inc(r) }
        }
    }

    override fun copy(reader: Reader): Reader =
        if (includesName != null) {
            registerIncludes(reader)
        }
        else {
            applyIncludes(reader)
        }
}
