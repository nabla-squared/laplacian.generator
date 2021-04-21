package laplacian.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class Laplacian: CliktCommand() {
    override fun run() = Unit
}

fun main(vararg args: String) {
    Laplacian()
        .subcommands(Generate())
        .main(args)
}
