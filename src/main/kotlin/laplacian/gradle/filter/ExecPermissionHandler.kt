package laplacian.gradle.filter

import laplacian.gradle.task.generate.ExecutionContext
import org.gradle.api.file.FileCopyDetails
import java.io.OutputStream
import java.io.Reader

class ExecPermissionHandler : FileCopyHandler {

    override fun handle(details: FileCopyDetails, context: ExecutionContext): Boolean {
        if (context.currentTemplate.canExecute() || details.name.endsWith(".sh")) {
            details.mode = 0b111_101_101 // 755
        }
        return false // This handler does not affect the content of files
    }

    override fun copy(reader: Reader, out: OutputStream) {
        TODO("not implemented")
    }
}
