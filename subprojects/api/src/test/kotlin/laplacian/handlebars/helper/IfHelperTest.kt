package laplacian.handlebars.helper

import laplacian.handlebars.HandlebarsUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertAll

class IfHelperTest {

    fun String.handlebars() = HandlebarsUtil.buildTemplate(this)

    @Test
    fun test_if_helper() {
        val template = """{{#if this}}TRUE{{else}}FALSE{{/if}}"""
        assertAll(
            { assertEquals("FALSE", template.handlebars().apply(null)) },
            { assertEquals("TRUE", template.handlebars().apply(1)) },
            { assertEquals("TRUE", template.handlebars().apply(listOf(""))) },
            { assertEquals("FALSE", template.handlebars().apply(0)) },
            { assertEquals("FALSE", template.handlebars().apply("")) },
            { assertEquals("FALSE", template.handlebars().apply(emptyList<String>())) }
        )
    }

    @Test
    fun test_if_helper_in_expression() {
        val template = """{{if this "TRUE" "FALSE"}}"""
        assertAll(
            { assertEquals("TRUE", template.handlebars().apply(1)) },
            { assertEquals("TRUE", template.handlebars().apply(listOf(""))) },
            { assertEquals("FALSE", template.handlebars().apply(0)) },
            { assertEquals("FALSE", template.handlebars().apply("")) },
            { assertEquals("FALSE", template.handlebars().apply(emptyList<String>())) }
        )
    }

    @Test
    fun test_if_helper_without_alternate_value() {
        val template = """{{if this "TRUE"}}"""
        assertAll(
            { assertEquals("TRUE", template.handlebars().apply(1)) },
            { assertEquals("", template.handlebars().apply(0)) }
        )
    }
}
