package laplacian.generate.copyhandler

import laplacian.generate.ExecutionContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class BinaryFileHandlerTest {
    @Test
    fun set_binary_flag_if_it_has_extesion_of_achive_file() {
        val jarFile = File("./libs/hoge.jar")
        val context = ExecutionContext()
        val fileCopyDetails = FileCopyDetails(jarFile, "", context)
        assertEquals(false, fileCopyDetails.binary)
        BinaryFileHandler().handle(fileCopyDetails)
        assertEquals(true, fileCopyDetails.binary)
    }
}
