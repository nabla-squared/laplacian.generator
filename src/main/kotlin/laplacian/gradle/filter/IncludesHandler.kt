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
            context.includeName = m.groupValues[2]
            details.name = filename
        }
        else {
            context.includeName = null
        }
        return true
    }

    private fun registerIncludes(includeReader: Reader): Reader {
        val includeName = context.includeName
        val included = includeReader.readText()
        val key = context.curretntTemlatePath.replace("@", "_")
        val path = context.currentTarget.path
        val include = { reader: Reader ->
            val regex = """(?<=^|\n)(.*)@(\+?)$includeName(\|${Regex.escape(key)}|)@(.*)(?=\n)([\s\S]*)\n(.*)@$includeName\3@""".toRegex()
            val originalContent = reader.readText()
            val m = regex.find(originalContent)
            val content = if (m == null) {
                originalContent
            } else {
                val v = m.groupValues
                val initialMatch = v[3].isBlank()
                val additive = v[2].isNotBlank()
                val identifier = if (additive) "|$key" else ""
                originalContent.replaceRange(
                    m.range,
                    "${v[1]}@${v[2]}$includeName$identifier@${v[4]}\n" +
                    "$included\n" +
                    "${v[6]}@$includeName$identifier@" +
                    if (initialMatch && additive)
                      "\n${v[1]}@+$includeName@${v[4]}\n" +
                      "${v[6]}@$includeName@"
                    else
                      ""
                )
            }
            StringReader(content)
        }
        registerIncludes(path, context.includeName!!, include)
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
        if (context.includeName != null) {
            registerIncludes(reader)
        }
        else {
            applyIncludes(reader)
        }
}
