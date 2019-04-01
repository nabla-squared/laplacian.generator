package laplacian.gradle.filter

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.gradle.api.file.FileCopyDetails
import java.io.OutputStream
import java.io.Reader

class PlantUmlCopyHandler: FileCopyHandler {

    companion object {
        val PLANT_UML_PATTERN = """(.*)\.(puml|xsd)$""".toRegex()
    }

    override fun handle(details: FileCopyDetails): Boolean {
        val m = PLANT_UML_PATTERN.matchEntire(details.name)
        if (m == null) return false
        val baseName = m.groups[1]!!.value
        details.name = baseName + ".svg"
        return true
    }

    override fun copy(reader: Reader, out: OutputStream) {
        val plantUml = reader.readText()
        SourceStringReader(plantUml).outputImage(out, FileFormatOption(FileFormat.SVG))
    }
}
