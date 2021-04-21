package laplacian.generate.util

import com.networknt.schema.ValidationMessage

class JsonSchemaValidationError(
    messages: Set<ValidationMessage>
): RuntimeException(messages.joinToString("\n") { it.message })

