package laplacian.util
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertAll

class StringTest {
    @Test
    fun test_underscolize() {
        assertEquals(
            "hoge_fuga_piyo", "hogeFugaPiyo".lowerUnderscorize()
        )
        assertEquals(
                "HOGE_FUGA_PIYO", "hoge fuga piyo".upperUnderscorize()
        )
    }

    @Test
    fun test_camelize() {
        assertEquals(
                "HogeFugaPiyo", "hogeFugaPiyo".upperCamelize()
        )
        assertEquals(
                "HogeFugaPiyo", "hoge fuga piyo".upperCamelize()
        )
        assertEquals(
                "HogeFugaPiyo", "hoge-fugaPiyo".upperCamelize()
        )
        assertEquals(
                "hogeFugaPiyo", "hoge fuga piyo".lowerCamelize()
        )
        assertEquals(
                "hogeFugaPiyo", "hoge<fuga_piyo>".lowerCamelize()
        )
        assertEquals(
                "listEntity", "List<Entity>".lowerCamelize()
        )
    }

    @Test
    fun test_hyphenize() {
        assertEquals(
                "hoge-fuga-piyo", "HogeFugaPiyo".lowerHyphenize()
        )
        assertEquals(
                "hoge-fuga-piyo", "HOGE_FUGA_PIYO".lowerHyphenize()
        )
        assertEquals(
                "HOGE-FUGA-PIYO", "HOGE_FUGA_PIYO".upperHyphenize()
        )
    }


    @Test
    fun test_pathify() {
        assertEquals(
                "hoge/fuga_piyo", "Hoge.FugaPiyo".pathify()
        )
        assertEquals(
                "hoge/fuga/piyo", "hoge.fuga.piyo".pathify()
        )
        assertEquals(
                "hoge/fuga_piyo", "HOGE.FUGA-PIYO".pathify()
        )
    }

    @Test
    fun test_strip_doc_comment() {
        val withDocument = """
        |/** hogehoge */
        |private val _hogehoge
        """.trimMargin()
        val actual = withDocument.stripDocComments()
        val expect = """
        |private val _hogehoge
        """.trimMargin()
        assertEquals(expect, actual)
    }

    @Test
    fun test_strip_other_style_of_comment() {
        val withDocument = """
        |#
        |# hogehoge
        |#
        |class Hogehoge {
        |    #
        |    # fugafuga
        |    #
        |    private val fugafuga # OK
        |}
        """.trimMargin()
        val actual = withDocument.stripDocComments()
        val expect = """
        |class Hogehoge {
        |    private val fugafuga # OK
        |}
        """.trimMargin()
        assertEquals(expect, actual)
    }

    @Test
    fun test_strip_doc_comment_including_line_break() {
        val withDocument = """
        |/**
        | * say hogehoge
        | */
        |fun hogehoge() {
        |    println("hogehoge")
        |}
        """.trimMargin()
        val actual = withDocument.stripDocComments()
        val expect = """
        |fun hogehoge() {
        |    println("hogehoge")
        |}
        """.trimMargin()
        assertEquals(expect, actual)
    }

    @Test
    fun test_strip_blank_lines() {
        val withDocument = """
        |/**
        | * say hogehoge
        | */
        |fun hogehoge() {
        |    println("hogehoge")
        |${"    "}
        |    println("fugafuga")
        |
        |}
        """.trimMargin()
        val actual = withDocument.stripBlankLines()
        val expect = """
        |/**
        | * say hogehoge
        | */
        |fun hogehoge() {
        |    println("hogehoge")
        |    println("fugafuga")
        |
        |}
        """.trimMargin()
        assertEquals(expect, actual)
    }

    @Test
    fun test_shift_a_text_block() {
        val codeBlock = """
        |const nextDeck = entities.decks[action.deckId]
        |
        |app.activeDeck = nextDeck
        |app.deckIndex = 0
        |app.cardFlipped = false
        """.trimMargin()
        val actual = """
        |    ${codeBlock.shift(4)}
        """.trimMargin()
        val expect = """
        |    const nextDeck = entities.decks[action.deckId]
        |
        |    app.activeDeck = nextDeck
        |    app.deckIndex = 0
        |    app.cardFlipped = false
        """.trimMargin()
        assertEquals(expect, actual)

        val actual2 = """
        |  ${codeBlock.shift(2)}
        """.trimMargin()
        val expect2 = """
        |  const nextDeck = entities.decks[action.deckId]
        |
        |  app.activeDeck = nextDeck
        |  app.deckIndex = 0
        |  app.cardFlipped = false
        """.trimMargin()
        assertEquals(expect2, actual2)
    }

    @Test
    fun test_shift_helper() {
        val codeBlock = """
        |const nextDeck = entities.decks[action.deckId]
        |
        |app.activeDeck = nextDeck
        |app.deckIndex = 0
        |app.cardFlipped = false
        """.trimMargin()
        val actual = "    {{shift this 4}}".handlebars().apply(codeBlock)
        val expect = """
        |    const nextDeck = entities.decks[action.deckId]
        |
        |    app.activeDeck = nextDeck
        |    app.deckIndex = 0
        |    app.cardFlipped = false
        """.trimMargin()
        assertEquals(expect, actual)
    }

    @Test
    fun test_shift_block_helper() {
        val codeBlock = """
        |{{#shift width=4}}
        |const nextDeck = entities.decks[action.deckId]
        |
        |app.activeDeck = nextDeck
        |app.deckIndex = 0
        |app.cardFlipped = false
        |{{/shift}}
        """.trimMargin()
        val actual = codeBlock.handlebars().apply("")
        val expect = """
        |const nextDeck = entities.decks[action.deckId]
        |
        |    app.activeDeck = nextDeck
        |    app.deckIndex = 0
        |    app.cardFlipped = false
        """.trimMargin()
        assertEquals(expect, actual)
    }

    @Test
    fun test_camelize_helper() {
        assertEquals("listEntity", "{{lower-camel this}}".handlebars().apply("List<Entity>"))
    }

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

    @Test
    fun test_helper_usage_in_subexpressions() {
        assertEquals("HOGE/FUGA/PIYO", "{{upper (path this)}}".handlebars().apply("hoge.fuga.piyo"))
    }

    @Test
    fun test_using_additional_string_helper() {
        assertEquals("HELLO", "{{upper this}}".handlebars().apply("hello"))
    }

    @Test
    fun test_trim_helper() {
        assertAll(
            { assertEquals("hello", "{{trim this}}".handlebars().apply("  hello\n")) },
            { assertEquals("hello", """{{#trim chars=","}}{{this}}{{/trim}}""".handlebars().apply("hello,")) },
            { assertEquals("hello", """{{#trim chars=","}}{{this}}{{/trim}}""".handlebars().apply("hello,  \n")) }
        )
    }

    @Test
    fun test_lookup_helper() {
        val context = mapOf(
            "map" to mapOf("a" to "AA", "b" to "BB"),
            "list" to listOf("A", "B")
        )
        assertAll(
            { assertEquals("AA", """{{define "key" "a"}}{{lookup map key}}""".handlebars().apply(context)) },
            { assertEquals("", """{{define "key" "c"}}{{lookup map key}}""".handlebars().apply(context)) },
            { assertEquals("A", """{{define "key" 0}}{{lookup list key}}""".handlebars().apply(context)) },
            { assertEquals("", """{{define "key" 2}}{{lookup list key}}""".handlebars().apply(context)) }
        )
    }

    @Test
    fun test_contains_key_helper() {
        val array = arrayOf("a", "b", "c")
        val list = listOf("a", "b", "c")
        val map = mapOf("a" to "AA", "b" to "BB", "c" to "CC")
        val context = mapOf(
            "array" to array,
            "list" to list,
            "map" to map
        )
        assertAll(
            { assertEquals("Y", "{{#if (contains-key array 0)}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("", "{{#if (contains-key array 3)}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("Y", "{{#if (contains-key list 0)}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("", "{{#if (contains-key list 3)}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("Y", "{{#if (contains-key map 'a')}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("", "{{#if (contains-key map 'd')}}Y{{/if}}".handlebars().apply(context)) }
        )
    }


    @Test
    fun test_trim_block_helper() {
        val codeBlock = """
        |{{#trim}}
        |{{#unless null}}
        |  hogehoge
        |{{/unless}}
        |{{/trim}}
        """.trimMargin()
        val actual = codeBlock.handlebars().apply("")
        val expect = "hogehoge"
        assertEquals(expect, actual)
    }

    @Test
    fun test_literal_helper() {
        val template = "{{literal this}}"
        assertAll(
            { assertEquals("\"hogehoge\"", template.handlebars().apply("hogehoge")) },
            { assertEquals("42", template.handlebars().apply(42)) },
            { assertEquals("\"42\"", template.handlebars().apply("42")) },
            { assertEquals("null", template.handlebars().apply(null)) },
            { assertEquals("""listOf("hoge", "fuga", "piyo")""",
              template.handlebars().apply(listOf("hoge", "fuga", "piyo"))) },
            { assertEquals("""mapOf("a" to "hoge", "b" to "fuga", "c" to "piyo")""",
              template.handlebars().apply(mapOf("a" to "hoge", "b" to "fuga", "c" to "piyo"))) }
        )
    }

    @Test
    fun test_yaml_helper() {
        val obj = mapOf(
            "H" to "hogehoge",
            "F" to false,
            "Z" to 0,
            "L" to listOf("hoge", "fuga", "piyo")
        )
        val template = """{{yaml this}}"""
        val templateWithPad = """> {{yaml this "> "}}"""
        assertAll({
            assertEquals("""
            |H: hogehoge
            |F: false
            |Z: 0
            |L:
            |- hoge
            |- fuga
            |- piyo
            """.trimMargin(), template.handlebars().apply(obj).trim())
        }, {
            assertEquals("""
            |> H: hogehoge
            |> F: false
            |> Z: 0
            |> L:
            |> - hoge
            |> - fuga
            |> - piyo
            """.trimMargin(), templateWithPad.handlebars().apply(obj).trim())
        })

    }

    @Test
    fun test_dquote_helper() {
        assertEquals(
            """"^(\"[^\"]+\"|\\{[a-zA-Z]+\\})$"""",
            "{{dquote this}}".handlebars().apply("""^("[^"]+"|\{[a-zA-Z]+\})$""")
        )
    }

    @Test
    fun test_dquote_block_helper() {
        assertEquals(
            """"^(\"[^\"]+\"|\\{[a-zA-Z]+\\})$"""",
            "{{#dquote}}{{this}}{{/dquote}}".handlebars().apply("""^("[^"]+"|\{[a-zA-Z]+\})$""")
        )
    }

    @Test
    fun test_join_helper() {
        assertEquals("hoge,fuga,piyo", "{{#block-join this as |item|}}{{item}}{{/block-join}}".handlebars().apply(arrayOf("hoge", "fuga", "piyo")))
        assertEquals("hoge|fuga|piyo", """{{#block-join this separator="|" as |item|}}{{item}}{{/block-join}}""".handlebars().apply(arrayOf("hoge", "fuga", "piyo")))
    }

    @Test
    fun test_unique_helper() {
        val context = arrayOf("hoge", "fuga", "hoge")
        assertEquals(
            "|hoge||fuga|",
            "{{#each (unique this) as |w|}}|{{w}}|{{/each}}".handlebars().apply(context)
        )
    }

    @Test
    fun test_concat_helper() {
        val context = mapOf(
            "A" to arrayOf("apple", "ape"),
            "B" to arrayOf("big", "bug")
        )
        assertEquals(
            "|apple||ape||big||bug|",
            "{{#each (concat this.A this.B) as |w|}}|{{w}}|{{/each}}".handlebars().apply(context)
        )
    }

    @Test
    fun test_map_helper() {
        val context = listOf(
            mapOf("A" to "apple", "B" to "banana", "C" to "candy"),
            mapOf("A" to "ape", "B" to "bull", "C" to "canary")
        )
        assertEquals(
            "|apple||ape|",
            "{{#each (map this 'A') as |w|}}|{{w}}|{{/each}}".handlebars().apply(context)
        )
        assertEquals(
            "|banana||bull|",
            "{{#each (map this 'B') as |w|}}|{{w}}|{{/each}}".handlebars().apply(context)
        )
    }

    @Test
    fun test_custom_each_helper() {
        val context = mapOf(
            "a" to "ORIGINAL_ITEM",
            "items" to listOf(
                mapOf("a" to "apple", "B" to "banana", "C" to "candy"),
                mapOf("a" to "ape", "B" to "bull", "C" to "canary")
            )
        )
        assertEquals(
            "|apple||ape|",
            "{{#each items}}|{{a}}|{{/each}}".handlebars().apply(context)
        )
        assertEquals(
            "|ORIGINAL_ITEM/apple||ORIGINAL_ITEM/ape|",
            "{{#each items as |w|}}|{{a}}/{{w.a}}|{{/each}}".handlebars().apply(context)
        )
    }

}
