package laplacian.generate.copyhandler

class BinaryFileHandler: FileCopyHandler {

    companion object {
        val BINARY_FILE_EXTENSIONS = """^.*\.(jar|exe|bin|g?zip|tar|t?gz|icon?|png|jpe?g|bmp|gif|tiff|xlsx?)$""".toRegex()
    }

    override fun handle(details: FileCopyDetails): Boolean {
        if (details.destFileName.matches(BINARY_FILE_EXTENSIONS)) {
            details.binary = true
            return false
        }
        return true
    }
}
