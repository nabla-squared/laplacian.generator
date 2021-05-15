package laplacian.generate.copyhandler

import com.fasterxml.jackson.databind.ObjectMapper
import laplacian.generate.ExecutionContext
import java.io.File

data class FileCopyDetails (
    val templateFile: File,
    val template: String,
    val context: ExecutionContext,
) {
    var destFileName: String = templateFile.name
    lateinit var destFileDir: String
    val destPath: String
        get() = "${destFileDir}/${destFileName}".replace("/+".toRegex(), "/")
    var exclude: Boolean = false
    var overwrite: Boolean = true
    var binary: Boolean = false
    var content: String = template
    var includeName: String? = null
    var canExecute: Boolean = templateFile.canExecute()
    var canWrite: Boolean = templateFile.canWrite()
    var canRead: Boolean = templateFile.canRead()

    fun copyTo(destRootDir: File, includes: List<FileCopyDetails> = emptyList()) {
        if (exclude) return
        val destDir = File(destRootDir, destFileDir)
        if (!destDir.exists() && !destDir.mkdirs()) throw IllegalStateException(
           "Failed to create a directory at ${destDir.absolutePath} while processing a template.: $this"
        )
        val destFile = File(destDir, destFileName)
        if (destFile.exists() && !overwrite) return
        if (!destFile.exists() && !destFile.createNewFile()) throw java.lang.IllegalStateException(
            "Failed to create a file at ${destFile.absolutePath} while processing a template.: $this"
        )
        if (binary) {
            templateFile.copyTo(destFile, true)
        }
        else {
            includes.forEach { processInclude(it) }
            destFile.writeText(content)
        }
        destFile.setReadable(canRead)
        destFile.setWritable(canWrite)
        destFile.setExecutable(canExecute)
    }

    private fun processInclude(include: FileCopyDetails): Unit {
        val key = include.templateFile.canonicalPath.replace("@", "_")
        val regex = """(?<=^|\n)(.*)@(\+?)${include.includeName}@(.*)(?=\n)([\s\S]*)\n(.*)@${include.includeName}@""".toRegex()
        val m = regex.find(content) ?: return
        val v = m.groupValues
        val additive = v[2].isNotBlank()
        //val identifier = "" //if (additive) "|$key" else ""
        val followingStartMarker = v[3]
        val originalContent = v[4]
        val precedingEndMarker = v[5]
        val newContent = include.content +
            if (additive)
                "\n${v[1]}@+${include.includeName}@${followingStartMarker}\n${precedingEndMarker}@${include.includeName}@"
            else ""
        val processed = content.replaceRange(m.range, newContent)
        content = processed
    }

    override fun toString(): String = objectMapper.writeValueAsString(this)

    companion object {
        val objectMapper = ObjectMapper()
    }
}
