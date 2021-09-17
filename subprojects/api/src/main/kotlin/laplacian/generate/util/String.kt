package laplacian.generate.util

import com.github.jknack.handlebars.*
import laplacian.handlebars.HandlebarsUtil
import org.atteo.evo.inflector.English
import java.io.File
import java.security.MessageDigest

val SEPARATOR = """([^a-zA-Z0-9$\u0080-\u9fff]+|(?<=[a-z0-9])(?=[A-Z]))""".toRegex()

fun splitIdentifierIntoTokens(str: String): List<String> {
    val noParenthesis = str.matches("""^[a-zA-Z0-9]+$""".toRegex())
    val tokens =
        if (noParenthesis && str.contains("""[a-z]""".toRegex()))
           str.replace("""(?<=[A-Z])([A-Z])""".toRegex(), " $1")
        else str
    return tokens.split(SEPARATOR)
}

fun String.capitalizeFirst() = when {
    this.isEmpty() -> ""
    else -> this[0].toUpperCase() + this.substring(1)
}

fun String.upperCamelize() =
        splitIdentifierIntoTokens(this)
            .filter{ it.isNotEmpty() }
            .map{ it[0].toUpperCase() + it.substring(1).toLowerCase() }
            .joinToString("")

fun String.lowerCamelize(): String = when {
    this.isEmpty() -> ""
    this.length == 1 -> this.substring(0, 1).toLowerCase()
    else -> this.upperCamelize().let {
        if (it.isEmpty()) "" else it[0].toLowerCase() + it.substring(1)
    }
}

fun joinTokens(str: String, separator: String, upperCase: Boolean): String {
    return splitIdentifierIntoTokens(str)
          .map{ if (upperCase) it.toUpperCase() else it.toLowerCase() }
          .joinToString(separator)
}

fun String.lowerUnderscorize() = joinTokens(this, "_", false)
fun String.upperUnderscorize() = joinTokens(this, "_", true)
fun String.lowerHyphenize() = joinTokens(this, "-", false)
fun String.upperHyphenize() = joinTokens(this, "-", true)
fun String.pathify() = split("""\.+""".toRegex()).map{ it.lowerUnderscorize() }.joinToString("/")
fun String.spaceDelimited() = joinTokens(this, " ", false)
fun String.dotDelimited() = joinTokens(this, ".", false)

fun String.stripBlankLines() =
        this.replace("""(^|\n)[ \t]+(?=$|\n)""".toRegex(), "")

val DOC_COMMENT = """(/\*\*[\s\S]*?\*+/|([ \t]*#.*(\n|$)){2,})""".toRegex()

fun String.stripDocComments() =
        this.replace(DOC_COMMENT, "")
            .stripBlankLines()
            .trim()

fun String.pluralize() = English.plural(this)

fun String.handlebars(
    helpers: Map<String, Helper<*>> = emptyMap(),
    basePath: File? = null
): Template = HandlebarsUtil.buildTemplate(this, helpers, basePath)

fun String.handlebarsForPath(
    helpers: Map<String, Helper<*>> = emptyMap(),
    basePath: File? = null
): Template = HandlebarsUtil.buildTemplateForPath(this, helpers, basePath)

fun String.shift(shiftWidth: Int): String =
    this.pad("".padEnd(shiftWidth, ' '))

fun String.pad(padString: String): String =
    this.trim().replace("""\n([^\n]*)""".toRegex()) { m ->
        "\n${padString}${m.groupValues[1]}".trimEnd(' ')
    }

fun String.dquote(): String =
    "\"${this.trim().replace("""["\\]""".toRegex(), "\\\\$0")}\""

fun String.md5(str: String): String =
    MessageDigest
        .getInstance("MD5")
        .digest(str.toByteArray())
        .joinToString("") { "%02x".format(it) }
