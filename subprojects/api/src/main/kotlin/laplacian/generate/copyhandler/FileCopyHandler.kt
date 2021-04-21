package laplacian.generate.copyhandler

interface FileCopyHandler {
    fun handle(details: FileCopyDetails): Boolean
}
