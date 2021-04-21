package laplacian.generate.copyhandler

class ExecPermissionHandler : FileCopyHandler {

    override fun handle(details: FileCopyDetails): Boolean {
        if (details.binary) return true
        if (details.content.startsWith(SHELL_BANG)) {
            details.canExecute = true
        }
        return true
    }

    companion object {
        const val SHELL_BANG = "#!"
    }
}
