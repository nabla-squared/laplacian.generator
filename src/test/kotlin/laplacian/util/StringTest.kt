package laplacian.util
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

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
    fun test_using_additonal_string_helper() {
        assertEquals("HELLO", "{{upper this}}".handlebars().apply("hello"))
    }

    @Test
    fun test_trim_helper() {
        assertEquals("hello", "{{trim this}}".handlebars().apply("  hello\n"))
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
