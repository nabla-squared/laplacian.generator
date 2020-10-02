package laplacian.gradle.task.generate.expression

import com.github.jknack.handlebars.Context
import laplacian.util.*
import org.gradle.api.GradleException
import java.io.File

interface ExpressionProcessor {

    fun eval(path: String, context: Context)

    var next: ExpressionProcessor

    companion object {

        private val COMMAND_EXPR = """(.*?)\{\s*(each|if|unless|with)\s+([^}]+?)(\s+as\s+([-_a-zA-Z0-9$]+))?\s*}""".toRegex()

        fun process(path: String, context: Map<String, Any?>): List<Pair<String, Context>> {
            return process(path, context.toContext())
        }
        /**
         * Process expressions included in the given [path].
         *
         * @return the list of pairs whose key is the expanded file path
         * and value is the model including scoped variables.
         */
        fun process(path: String, context: Context): List<Pair<String, Context>> {
            val processors = arrayListOf<ExpressionProcessor>()
            var prev: ExpressionProcessor? = null
            val addProcessor = { next: ExpressionProcessor ->
                if (prev != null) prev!!.next = next
                processors.add(next)
                prev = next
            }

            val remaining = COMMAND_EXPR.replace(path) { m ->
                val (leading, commandName, valueExpr, _, varName) = m.destructured
                if (!leading.isEmpty()) {
                    addProcessor(Expression(leading))
                }
                if (commandName == "each") {
                    if (valueExpr.isEmpty()) throw IllegalArgumentException(
                        "items must not be null: $valueExpr in $path"
                    )
                    if (varName.isEmpty()) throw IllegalArgumentException(
                        "the name of variable which receives each item: $path"
                    )
                    addProcessor(EachCommand(valueExpr, varName))
                }
                if (commandName == "if") {
                    addProcessor(IfCommand(valueExpr))
                }
                if (commandName == "unless") {
                    addProcessor(IfCommand(valueExpr, true))
                }
                if (commandName == "with") {
                    addProcessor(WithCommand(valueExpr, varName))
                }
                ""
            }
            if (!remaining.isEmpty()) {
                addProcessor(Expression(remaining))
            }
            val terminator = Terminator()
            addProcessor(terminator)
            try {
                processors.first().eval("", context)
                return terminator.results
            }
            catch (e: RuntimeException) {
                var modelDumpFile: File? = null
                try {
                    modelDumpFile = createTempFile("laplacian-generator-model-dump-", ".json")
                    modelDumpFile.writeText(context.toString())
                }
                catch(ignored: Exception) { /* ignore */}
                throw GradleException(
                    "A problem occurred while expanding path: $path, cause: ${e.message}\n" +
                    "Generator model was dumped in the file at: ${modelDumpFile?.absolutePath}",
                    e
                )
            }
        }
    }
}
