package laplacian.generate.util

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
                "HogeFugaPiyo", "HOGE_FUGA_PIYO".upperCamelize()
        )

        assertEquals(
                "", "".upperCamelize()
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
        assertEquals(
                "", "".lowerCamelize()
        )
        assertEquals(
                "a", "A".lowerCamelize()
        )
        assertEquals(
                "a", "a".lowerCamelize()
        )
        assertEquals(
                "", "{}".lowerCamelize()
        )
    }

    @Test
    fun test_camelize_text_containing_1_character_token() {
        assertEquals(
            "HogeFugaPiyoP", "HOGE_FUGA_PIYO_P".upperCamelize()
        )
        assertEquals(
            "HogeFugaNPiyo", "HOGE_FUGA_N_PIYO".upperCamelize()
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
        assertEquals(
                "新橋-京急蒲田", "新橋 京急蒲田".upperHyphenize()
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
    fun test_dot_delimited() {
        assertEquals(
                "hoge.fuga.piyo", "Hoge FugaPiyo".dotDelimited()
        )
        assertEquals(
                "hoge.fuga.piyo", "HOGE.FUGA-PIYO".dotDelimited()
        )
    }

    @Test
    fun test_space_delimited() {
        assertEquals(
                "hoge fuga piyo", "Hoge.FugaPiyo".spaceDelimited()
        )
        assertEquals(
                "hoge fuga piyo", "HOGE.FUGA-PIYO".spaceDelimited()
        )
    }

    @Test
    fun test_capitalize_first() {
        assertEquals(
                "Capitalize First", "capitalize First".capitalizeFirst()
        )
        assertEquals(
                "C", "c".capitalizeFirst()
        )
        assertEquals(
                "", "".capitalizeFirst()
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
}
