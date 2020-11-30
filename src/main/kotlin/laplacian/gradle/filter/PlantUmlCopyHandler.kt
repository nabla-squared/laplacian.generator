package laplacian.gradle.filter

import laplacian.gradle.task.generate.ExecutionContext
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.gradle.api.file.FileCopyDetails
import java.io.*

class PlantUmlCopyHandler: FileCopyHandler {

    companion object {
        val PLANT_UML_PATTERN = """(.*)\.(puml|xsd)$""".toRegex()
    }

    private val fileFormat: FileFormatOption? by lazy {
        try {
            FileFormatOption(FileFormat.SVG)
        }
        catch (e: Throwable) {
            println(e.message)
            null
        }
    }

    override fun handle(details: FileCopyDetails, context: ExecutionContext): Boolean {
        if (fileFormat == null) return false
        val m = PLANT_UML_PATTERN.matchEntire(details.name)
        if (m == null) return false
        val baseName = m.groups[1]!!.value
        details.name = baseName + ".svg"
        return true
    }

    override fun copy(reader: Reader): Reader {
        val plantUml = reader.readText()
        val buffer = ByteArrayOutputStream()
        SourceStringReader(plantUml)
            .outputImage(buffer, fileFormat)
        return InputStreamReader(ByteArrayInputStream(buffer.toByteArray()))
    }
}
