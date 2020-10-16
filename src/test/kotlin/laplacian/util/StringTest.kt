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

    fun test_dot_delimited() {
        assertEquals(
                "hoge.fuga.piyo", "Hoge FugaPiyo".dotDelimited()
        )
        assertEquals(
                "hoge.fuga.piyo", "HOGE.FUGA-PIYO".dotDelimited()
        )
    }

    fun test_space_delimited() {
        assertEquals(
                "hoge fuga piyo", "Hoge.FugaPiyo".spaceDelimited()
        )
        assertEquals(
                "hoge fuga piyo", "HOGE.FUGA-PIYO".spaceDelimited()
        )
    }


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
    fun test_case_helper_in_expression() {
        val template = """{{case (eq this "A") "A!!" (eq this "B") "B!!" "UNDEFINED!!!"}}"""
        assertAll(
            { assertEquals("A!!", template.handlebars().apply("A")) },
            { assertEquals("B!!", template.handlebars().apply("B")) },
            { assertEquals("UNDEFINED!!!", template.handlebars().apply("C")) },
            { assertEquals("UNDEFINED!!!", template.handlebars().apply(0)) },
            { assertEquals("UNDEFINED!!!", template.handlebars().apply("")) },
            { assertEquals("UNDEFINED!!!", template.handlebars().apply(null)) }
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

    @Test
    fun test_eq_helper_null_handling() {
        val template = """{{if (eq this null) "IS NULL" "NOT NULL"}}"""
        assertAll(
            { assertEquals("IS NULL", template.handlebars().apply(null)) },
            { assertEquals("NOT NULL", template.handlebars().apply("")) }
        )
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
    fun test_printf_helper() {
        assertEquals(
            "MESSAGE: Hello",
            "{{printf 'MESSAGE: %s' (upper-camel this)}}".handlebars().apply("hello")
        )
        assertEquals(
            "MESSAGE: Hello SIZE: 5",
            "{{printf 'MESSAGE: %s SIZE: %d' (upper-camel this) this.length}}".handlebars().apply("hello")
        )
    }

    @Test
    fun test_replace_helper() {
        assertEquals(
            "piyo piyo piyo",
            "{{replace 'hoge hoge hoge' 'hoge' 'piyo'}}".handlebars().apply("")
        )
        assertEquals(
            "piyo hoge hoge",
            "{{replace 'hoge hoge hoge' '^hoge' 'piyo'}}".handlebars().apply("")
        )
        assertEquals(
            "\${piyo} hoge hoge",
            "{{replace 'hoge hoge hoge' '^hoge' '\\\${piyo}'}}".handlebars().apply("")
        )
        assertEquals(
            "poge hoge hoge",
            "{{replace 'hoge hoge hoge' '^h(oge)?' 'p\$1'}}".handlebars().apply("")
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
        val list = listOf("a", "b", "c", "")
        val map = mapOf("a" to "AA", "b" to "BB", "c" to "CC")
        val context = mapOf(
            "array" to array,
            "list" to list,
            "map" to map,
            "FALSE" to false
        )
        assertAll(
            { assertEquals("Y", "{{#if (contains-key array 0)}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("", "{{#if (contains-key array 3)}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("Y", "{{#if (contains-key list 0)}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("Y", "{{#if (contains-key list 3)}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("", "{{#if (contains-key list 4)}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("Y", "{{#if (contains-key map 'a')}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("", "{{#if (contains-key map 'd')}}Y{{/if}}".handlebars().apply(context)) },
            { assertEquals("Y", "{{#if (contains-key this 'FALSE')}}Y{{/if}}".handlebars().apply(context)) }
        )
    }

    @Test
    fun test_starts_with_helper() {
        val template = "{{#if (starts-with this '_')}}Private{{else}}Public{{/if}}"
        assertEquals("Private", template.handlebars().apply("_method_name"))
        assertEquals("Public", template.handlebars().apply("method_name"))

        val template2 = "{{starts-with this '_'}}"
        assertEquals("method_name", template2.handlebars().apply("_method_name"))
    }

    @Test
    fun test_ends_with_helper() {
        val template = "{{#if (ends-with this '.tgz')}}Tarball{{else}}Not supported{{/if}}"
        assertEquals("Tarball", template.handlebars().apply("archive.tgz"))
        assertEquals("Not supported", template.handlebars().apply("archive.7z"))

        val template2 = "{{ends-with this '.tgz'}}"
        assertEquals("archive", template2.handlebars().apply("archive.tgz"))
    }

    @Test
    fun test_contains_with_helper() {
        val template = "{{#if (contains this '\${')}}Invalid{{else}}Valid{{/if}}"
        assertEquals("Valid", template.handlebars().apply("\$method_name"))
        assertEquals("Invalid", template.handlebars().apply("method\${name}"))
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
    fun test_json_helper() {
        val obj = mapOf(
            "H" to "hogehoge",
            "F" to false,
            "Z" to 0,
            "L" to listOf("hoge", "fuga", "piyo")
        )
        val template = """{{json this}}"""
        val templateWithPad = """> {{json this "> "}}"""
        assertAll({
            assertEquals("""
            |{"H":"hogehoge","F":false,"Z":0,"L":["hoge","fuga","piyo"]}
            """.trimMargin(), template.handlebars().apply(obj).trim())
        }, {
            assertEquals("""
            |> {
            |>   "H" : "hogehoge",
            |>   "F" : false,
            |>   "Z" : 0,
            |>   "L" : [ "hoge", "fuga", "piyo" ]
            |> }
            """.trimMargin(), templateWithPad.handlebars().apply(obj).trim())
        })
    }

    @Test
    fun test_eval_template_helper() {
        val context = mapOf(
            "keywords" to listOf("hoge", "fuga", "piyo"),
            "message" to "{{#each keywords as |k|}}{{upper-snake k}}!{{#unless @last}}, {{/unless}}{{/each}}"
        )
        assertEquals("""
        |HOGE!, FUGA!, PIYO!
        """.trimMargin(), "{{eval-template message}}".handlebars().apply(context).trim())
    }

    @Test
    fun test_eval_expression_helper() {
        val context = mapOf(
            "keywords" to listOf("hoge", "fuga", "piyo"),
            "expression" to "(join keywords ' // ')"
        )
        assertEquals("""
        |hoge // fuga // piyo
        """.trimMargin(), "{{eval-expression expression}}".handlebars().apply(context).trim())
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
    fun test_list_of_helper() {
        val template = "{{define 'list' (list-of 'a' 'b' 'c')}}{{#each list as |item|}}{{item}}{{/each}}"
        val template2 = "{{#each (list-of 'a' 'b' 'c') as |item|}}{{item}}{{/each}}"
        assertEquals("abc", template.handlebars().apply(""))
        assertEquals("abc", template2.handlebars().apply(""))
    }

    @Test
    fun test_create_empty_list_with_list_of_helper() {
        val template = "{{define 'list' (list-of)}}{{#if list}}empty!{{else}}not empty!{{/if}}"
        val template2 = "{{define 'list' (list-of)}}{{#each list as |item|}}{{item}}{{/each}}"
        assertEquals("empty!", template.handlebars().apply(""))
        assertEquals("", template2.handlebars().apply(""))
    }

    @Test
    fun test_first_helper() {
        val context = mapOf(
            "str" to """
                |Summary
                |- At first
                |- In Addition
                """.trimMargin(),
            "arr" to listOf(
                "Summary",
                "- At first",
                "- In Addition"
            )
        );
        assertAll({
            assertEquals("Summary", "{{first str}}".handlebars().apply(context))
        }, {
            assertEquals("Summary", "{{first arr}}".handlebars().apply(context))
        })
    }

    @Test
    fun test_concat_helper_applied_to_arrays() {
        val context = mapOf(
            "A" to arrayOf("apple", "ape"),
            "B" to arrayOf("big", "bug"),
            "empty" to emptyArray()
        )
        assertEquals(
            "|apple||ape||big||bug|",
            "{{#each (concat this.A this.B) as |w|}}|{{w}}|{{/each}}".handlebars().apply(context)
        )
        assertEquals(
            "|apple||ape|",
            "{{#each (concat this.A this.empty) as |w|}}|{{w}}|{{/each}}".handlebars().apply(context)
        )
        assertEquals(
            "|big||bug|",
            "{{#each (concat this.empty this.B ) as |w|}}|{{w}}|{{/each}}".handlebars().apply(context)
        )
    }

    @Test
    fun test_concat_helper_applied_to_strings() {
        assertEquals(
            "|apple||ape||big||bug|",
            "{{concat '|apple|' '|ape|' '|big|' '|bug|'}}".handlebars().apply(emptyMap<String, Any>())
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
            "{{#each (map this '@it.A') as |w|}}|{{w}}|{{/each}}".handlebars().apply(context)
        )
        assertEquals(
            "|banana||bull|",
            "{{#each (map this '@it.B') as |w|}}|{{w}}|{{/each}}".handlebars().apply(context)
        )
        assertEquals(
            "|banana-candy||bull-canary|",
            """{{#each (map this '(concat @it.B "-" @it.C)') as |w|}}|{{w}}|{{/each}}""".handlebars().apply(context)
        )
        assertEquals(
            "|3||3|",
            """{{#each (map this '@it.size') as |w|}}|{{w}}|{{/each}}""".handlebars().apply(context)
        )
    }

    @Test
    fun test_filter_helper() {
        val context = listOf(
            "hogehoge", "fuga", "piyopiyo"
        )
        assertEquals(
            "hogehoge, piyopiyo",
            """{{join (filter this '(neq @it "fuga")') ', ' }}""".handlebars().apply(context)
        )
    }

    @Test
    fun test_any_helper() {
        val context = listOf(
            "hoge", "fuga", "piyo"
        )
        assertEquals(
            "OK",
            """{{if (any this '(eq @it "hoge")') 'OK' 'NG'}}""".handlebars().apply(context)
        )
        assertEquals(
            "NG",
            """{{if (any this '(eq @it "zap")') 'OK' 'NG'}}""".handlebars().apply(context)
        )
    }

    @Test
    fun test_sort_helper() {
        data class Item(
            private val _name: String,
            private val _price: Int
        ) {
            fun getName() = _name
            fun getPrice() = _price
        }
        val itemList = listOf(
            Item("beer", 2000),
            Item("car", 9000),
            Item("antenna", 5000)
        )
        assertAll({
            val context = listOf("100", "010", "002", "0001")
            assertEquals(
                "0001 002 010 100",
                """{{join (sort this) ' ' }}""".handlebars().apply(context)
            )
        }, {
            assertEquals(
                "antenna beer car",
                """{{join (map (sort this '@it.name') '@it.name' ) ' ' }}""".handlebars().apply(itemList)
            )
        }, {
            assertEquals(
                "beer antenna car",
                """{{join (map (sort this '@it.price') '@it.name' ) ' ' }}""".handlebars().apply(itemList)
            )
        })
    }

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

    @Test
    fun test_fake_date() {
        val fakeDate = "{{fake 'date-in-last-decade'}}".handlebars().apply("")
        assertTrue(
            fakeDate.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$".toRegex())
        )
        val fakeDateTime = "{{fake 'date-in-last-decade' 'uuuu/MM/dd hh:mm'}}".handlebars().apply("")
        assertTrue(
            fakeDateTime.matches("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}$".toRegex())
        )
    }
}
