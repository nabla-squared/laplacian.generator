package laplacian.generate.copyhandler

class BinaryFileHandler: FileCopyHandler {

    companion object {
        val BINARY_FILE_EXTENSIONS = """^.*\.(jar|exe|bin|zip|gzip|tar|tgz|gz)$""".toRegex()
    }

    override fun handle(details: FileCopyDetails): Boolean {
        if (details.destFileName.matches(BINARY_FILE_EXTENSIONS)) {
            details.binary = true
            return false
        }
        return true
    }
}
