package laplacian.gradle.filter

import org.gradle.api.file.FileCopyDetails
import java.io.OutputStream
import java.io.Reader
import laplacian.gradle.task.generate.ExecutionContext

interface FileCopyHandler {
    fun handle(details: FileCopyDetails, context: ExecutionContext) : Boolean
    fun copy(reader: Reader, out: OutputStream)
}
