package laplacian.generate.copyhandler

class IncludesHandler: FileCopyHandler {

    companion object {
        val TEMPLATE_PATTERN = """(.+)@([-_0-9a-zA-Z]+)@([^@]*)$""".toRegex()
    }

    override fun handle(details: FileCopyDetails): Boolean {
        val m = TEMPLATE_PATTERN.matchEntire(details.destFileName) ?: return true
        val filename = m.groupValues[1] + m.groupValues[3]
        val includeName = m.groupValues[2]
        details.destFileName = filename
        details.includeName = includeName
        //details.exclude = true
        return true
    }
}
