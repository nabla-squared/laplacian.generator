package laplacian.gradle.task.generate

import laplacian.gradle.filter.HandlebarsFilter
import laplacian.gradle.filter.PlantUmlFilter
import org.gradle.api.Project
import org.gradle.api.file.*
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory

class TemplateSpec(
    project: Project,
    val base: FileResourceSpec = FileResourceSpecBase(
        project, arrayOf("template"), "template"
    )
) : FileResourceSpec by base  {

    companion object {
        val TEMPLATE_GLOB = arrayOf("**/*.hbs.*", "**/*.hbs")
        val TEMPLATE_PATTERN = """(.*)(?:\.hbs\.(.+)|\.(.+)\.hbs)$""".toPattern()
        val REPLACED_FILE_NAME = "$1.$2$3"
        val PLANT_UML_GLOB = arrayOf("**/*.puml", "**/*.xsd")
        val PLANT_UML_PATTERN = """(.*)\.(puml|xsd)$""".toPattern()
        val PLANT_UML_REPLACED = "$1.svg"
    }

    @Optional
    @OutputDirectory
    val into = project.objects
              .property(String::class.java)
              .value("./")

    fun into(path: String) {
        into.set(path)
    }

    fun applyTo(copySpec: CopySpec, filterOpts: Map<String, Any>) {
        base.forEachFileSets { templateFiles ->
            copySpec.into(into.get())
            copySpec.from(templateFiles) {
                it.exclude(*TEMPLATE_GLOB, "META-INF/**")
            }
            copySpec.from(templateFiles) {
                it.include(*TEMPLATE_GLOB)
                it.exclude("META-INF/**")
                it.filter(filterOpts, HandlebarsFilter::class.java)
                it.rename(TEMPLATE_PATTERN, REPLACED_FILE_NAME)
            }
            copySpec.from(templateFiles) {
                it.include(*PLANT_UML_GLOB)
                it.filter(PlantUmlFilter::class.java)
                it.rename(PLANT_UML_PATTERN, PLANT_UML_REPLACED)
            }
        }
    }
}
