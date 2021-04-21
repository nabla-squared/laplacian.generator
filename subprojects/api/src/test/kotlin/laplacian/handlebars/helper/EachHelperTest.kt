package laplacian.handlebars.helper

import laplacian.handlebars.HandlebarsUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class EachHelperTest {

    fun String.handlebars() = HandlebarsUtil.buildTemplate(this)

    @Test
    fun test_custom_each_helper() {
        val context = mapOf(
            "a" to "ORIGINAL_ITEM",
            "items" to listOf(
                mapOf("a" to "apple", "B" to "banana", "C" to "candy"),
                mapOf("a" to "ape", "B" to "bull", "C" to "canary")
            ),
            "lines" to """
            |apple
            |banana
            |candy
            """.trimMargin()
        )
        assertEquals(
            "|apple||ape|",
            "{{#each items}}|{{a}}|{{/each}}".handlebars().apply(context)
        )
        assertEquals(
            "|ORIGINAL_ITEM/apple||ORIGINAL_ITEM/ape|",
            "{{#each items as |w|}}|{{a}}/{{w.a}}|{{/each}}".handlebars().apply(context)
        )
        assertEquals(
            "|apple||banana||candy|",
            "{{#each lines as |line|}}|{{line}}|{{/each}}".handlebars().apply(context)
        )
    }
}
