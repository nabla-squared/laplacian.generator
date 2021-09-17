package laplacian.generate.copyhandler

class IgnoreFileHandler: FileCopyHandler {
    companion object {
        val EXCLUDED_PATTERNS = listOf(
            """^META-INF/""".toRegex(),
            """(^|\./).*\.partial\.hbs$""".toRegex()
        )
    }
    override fun handle(details: FileCopyDetails): Boolean {
        val path = "${details.destFileDir}/${details.destFileName}"
        if (EXCLUDED_PATTERNS.any{path.matches(it)}) {
            details.exclude = true
            return false
        }
        return true
    }
}
