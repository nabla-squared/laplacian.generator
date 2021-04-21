package laplacian.handlebars.helper

import laplacian.handlebars.HandlebarsUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DefineHelperTest {

    fun String.handlebars() = HandlebarsUtil.buildTemplate(this)

    @Test
    fun test_define_helper() {
        val codeBlock = """
        |{{define "v" 42}}
        |{{#define "hoge"}}fuga{{/define}}
        |value = {{v}}
        |hoge = {{hoge}}
        """.trimMargin()
        val actual = codeBlock.handlebars().apply("").trim()
        val expect = """
        |value = 42
        |hoge = fuga
        """.trimMargin().trim()
        assertEquals(expect, actual)
    }

}
