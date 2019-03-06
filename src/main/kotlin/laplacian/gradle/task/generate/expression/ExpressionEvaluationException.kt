package laplacian.gradle.task.generate.expression

import org.gradle.api.GradleException

class ExpressionEvaluationException(
    message: String,
    cause: Exception
): GradleException(message, cause)
