package laplacian.gradle.filter

import org.gradle.api.file.FileCopyDetails
import java.io.OutputStream
import java.io.Reader

interface FileCopyHandler {
    fun handle(details: FileCopyDetails) : Boolean
    fun copy(reader: Reader, out: OutputStream)
}
