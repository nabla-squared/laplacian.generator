package laplacian.gradle.task.expression

import com.github.jknack.handlebars.Context

class Terminator : ExpressionProcessor {

    override lateinit var next: ExpressionProcessor

    val results = arrayListOf<Pair<String, Context>>()

    override fun eval(path: String, context: Context) {
        results.add((path to context))
    }
}
