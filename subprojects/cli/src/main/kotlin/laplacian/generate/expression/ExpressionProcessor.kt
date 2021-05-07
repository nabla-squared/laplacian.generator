package laplacian.generate.expression

import com.github.jknack.handlebars.Context
import laplacian.generate.ExecutionContext

interface ExpressionProcessor {

    fun eval(path: String, context: Context)

    var next: ExpressionProcessor

    companion object {

        private val COMMAND_EXPR = """(.*?)\{\s*(each|if|unless|with)\s+([^}]+?)(\s+as\s+([-_a-zA-Z0-9$]+))?\s*}""".toRegex()

        /**
         * Process expressions included in the given [path].
         *
         * @return the list of pairs whose key is the expanded file path
         * and value is the model including scoped variables.
         */
        fun process(path: String, executionContext: ExecutionContext): List<Pair<String, Context>> {
            val processors = arrayListOf<ExpressionProcessor>()
            var prev: ExpressionProcessor? = null
            val addProcessor = { next: ExpressionProcessor ->
                if (prev != null) prev!!.next = next
                processors.add(next)
                prev = next
            }

            val remaining = COMMAND_EXPR.replace(path) { m ->
                val (leading, commandName, valueExpr, _, varName) = m.destructured
                if (leading.isNotEmpty()) {
                    addProcessor(Expression(leading, executionContext))
                }
                if (commandName == "each") {
                    if (valueExpr.isEmpty()) throw IllegalArgumentException(
                        "The items parameter of each command must not be null.\n" +
                        "valueExpression=[$valueExpr],\n" +
                        "path=[$path]\n"
                    )
                    if (varName.isEmpty()) throw IllegalArgumentException(
                        "The variable name (following keyword 'as') of each command is required.\n" +
                        "path=[$path]."
                    )
                    addProcessor(EachCommand(valueExpr, varName, executionContext))
                }
                if (commandName == "if") {
                    addProcessor(IfCommand(valueExpr, false, executionContext))
                }
                if (commandName == "unless") {
                    addProcessor(IfCommand(valueExpr, true, executionContext))
                }
                if (commandName == "with") {
                    addProcessor(WithCommand(valueExpr, varName, executionContext))
                }
                ""
            }
            if (remaining.isNotEmpty()) {
                addProcessor(Expression(remaining, executionContext))
            }
            val terminator = Terminator()
            addProcessor(terminator)
            try {
                processors.first().eval("", executionContext.baseModel)
                return terminator.results
            }
            catch (e: RuntimeException) {
                throw RuntimeException(
                    "A problem occurred while expanding path: $path, cause: ${e.message}"
                )
            }
        }
    }
}
