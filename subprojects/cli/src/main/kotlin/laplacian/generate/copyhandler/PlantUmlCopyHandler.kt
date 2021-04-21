package laplacian.generate.copyhandler

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
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

    override fun handle(details: FileCopyDetails): Boolean {
        if (fileFormat == null) return true
        val m = PLANT_UML_PATTERN.matchEntire(details.destFileName) ?: return true
        val baseName = m.groups[1]!!.value
        details.destFileName = "$baseName.svg"
        val plantUml = details.content
        val buffer = ByteArrayOutputStream()
        SourceStringReader(plantUml).outputImage(buffer, fileFormat)
        details.content = InputStreamReader(ByteArrayInputStream(buffer.toByteArray())).readText()
        return true
    }
}
