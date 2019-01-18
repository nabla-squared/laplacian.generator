package laplacian.metamodel.template

import org.junit.jupiter.api.Test

class ModelSchemaJson {

    val template = assertion.withTemplate(
        "template/metamodel/schema/{lower-underscore project.name}.json.hbs"
    )

    @Test
    fun test_generating_model_schema_file() {
        template.assertSameContent("schema/metamodel.json")
    }
}
