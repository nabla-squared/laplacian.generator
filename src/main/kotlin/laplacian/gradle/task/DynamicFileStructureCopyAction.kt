package laplacian.gradle.task

import laplacian.gradle.task.expression.ExpressionProcessor
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
    val executionContext: ProjectTemplate.ExecutionContext
) : CopyAction {

    override fun execute(stream: CopyActionProcessingStream): WorkResult {
        val action = InternalAction(fileResolver, executionContext)
        stream.process(action)
        return WorkResults.didWork(action.didWork)
    }

    class InternalAction (
        val fileResolver: PathToFileResolver,
        val executionContext: ProjectTemplate.ExecutionContext
    ) : CopyActionProcessingStreamAction {

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
