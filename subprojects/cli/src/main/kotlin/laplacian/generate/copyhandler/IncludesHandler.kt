package laplacian.generate.copyhandler

class IncludesHandler: FileCopyHandler {

    companion object {
        val TEMPLATE_PATTERN = """(.+)@([-_0-9a-zA-Z]+)@([^@]*)$""".toRegex()
    }

    override fun handle(details: FileCopyDetails): Boolean {
        val m = TEMPLATE_PATTERN.matchEntire(details.destFileName) ?: return true
        val filename = m.groupValues[1] + m.groupValues[3]
        val includeName = m.groupValues[2]
        details.destFileName = filename
        details.includeName = includeName
        details.exclude = true
        return true
    }
/*
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

 */
}
