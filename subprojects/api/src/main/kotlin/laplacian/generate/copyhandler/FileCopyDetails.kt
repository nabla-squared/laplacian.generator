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

    fun copyTo(destRootDir: File/*, includes: List<FileCopyDetails> = emptyList()*/) {
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
            setFileAttributesTo(destFile)
        }
        else if (includeName != null) {
            processInclude(this, destFile)
        }
        else {
            destFile.writeText(content)
            setFileAttributesTo(destFile)
        }
    }

    private fun setFileAttributesTo(destFile: File) {
        if (canRead != destFile.canRead()) {
            destFile.setReadable(canRead)
        }
        if (canWrite != destFile.canWrite()) {
            destFile.setWritable(canWrite)
        }
        if (canExecute != destFile.canExecute()) {
            destFile.setExecutable(canExecute)
        }
    }

    override fun toString(): String = objectMapper.writeValueAsString(this)

    companion object {
        val objectMapper = ObjectMapper()
        private fun processInclude(include: FileCopyDetails, destFile: File): Unit {
            //val key = include.templateFile.canonicalPath.replace("@", "_")
            val regex = """(?<=^|\n)(.*)@(\+?)${include.includeName}@(.*)(?=\n)([\s\S]*)\n(.*)@${include.includeName}@""".toRegex()
            val content = destFile.readText()
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
            destFile.writeText(processed)
        }
    }
}
