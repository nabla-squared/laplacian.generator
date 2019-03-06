package laplacian.gradle.task.generate

import laplacian.gradle.task.generate.expression.ExpressionProcessor
import org.gradle.api.GradleException
import org.gradle.api.internal.file.CopyActionProcessingStreamAction
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.internal.file.copy.CopyActionProcessingStream
import org.gradle.api.internal.file.copy.FileCopyDetailsInternal
import org.gradle.api.tasks.WorkResult
import org.gradle.api.tasks.WorkResults
import org.gradle.internal.file.PathToFileResolver
import java.io.File

class DynamicFileStructureCopyAction(
    val fileResolver: PathToFileResolver,
    val executionContext: ExecutionContext
): CopyAction {

    override fun execute(stream: CopyActionProcessingStream): WorkResult {
        val action = InternalAction(fileResolver, executionContext)
        try {
            stream.process(action)
        }
        catch (e: GradleException) {
            if (e.cause is GradleException) throw e.cause as GradleException
            throw e
        }
        return WorkResults.didWork(action.didWork)
    }

    class InternalAction (
        val fileResolver: PathToFileResolver,
        val executionContext: ExecutionContext
    ): CopyActionProcessingStreamAction {

        var didWork: Boolean = false

        override fun processFile(details: FileCopyDetailsInternal) {
            val target = fileResolver.resolve(details.relativePath.pathString)
            val expandedPaths = ExpressionProcessor.process(
                target.absolutePath,
                executionContext.baseModel
            )
            val copied = expandedPaths.all { (path, context) ->
                executionContext.currentModel = context
                details.copyTo(File(path))
            }
            if (copied) didWork = true
        }
    }


}
