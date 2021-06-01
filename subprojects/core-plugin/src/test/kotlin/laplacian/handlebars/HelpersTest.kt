package laplacian.handlebars

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertAll

class HelpersTest {

    fun String.handlebars() = HandlebarsUtil.buildTemplate(this, helpers = Helpers.helpers())

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
    fun test_pad_helper() {
        val codeBlock = """
        |const nextDeck = entities.decks[action.deckId]
        |
        |app.activeDeck = nextDeck
        |app.deckIndex = 0
        |app.cardFlipped = false
        """.trimMargin()
        val actual = """
        |/**
        | * {{pad this ' * '}}
        | */
        """.trimMargin().handlebars().apply(codeBlock)
        val expect = """
        |/**
        | * const nextDeck = entities.decks[action.deckId]
        | *
        | * app.activeDeck = nextDeck
        | * app.deckIndex = 0
        | * app.cardFlipped = false
        | */
        """.trimMargin()
        assertEquals(expect, actual)
    }

    @Test
    fun test_camelize_helper() {
        assertEquals("listEntity", "{{lower-camel this}}".handlebars().apply("List<Entity>"))
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
    fun test_eq_helper_null_handling() {
        val template = """{{if (eq this null) "IS NULL" "NOT NULL"}}"""
        assertAll(
            { assertEquals("IS NULL", template.handlebars().apply(null)) },
            { assertEquals("NOT NULL", template.handlebars().apply("")) }
        )
    }

    @Test
    fun test_not_null_helper() {
        val template = """{{if (not-null this) "NOT NULL" "IS NULL"}}"""
        assertAll(
            { assertEquals("IS NULL", template.handlebars().apply(null)) },
            { assertEquals("NOT NULL", template.handlebars().apply("")) }
        )
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
    fun test_java_property_value_helper() {
        val codeBlock = """
        |app.prop1={{java-property-value prop1}}
        |app.prop2={{java-property-value prop2}}
        |app.prop3={{java-property-value prop3}}
        |app.prop4={{java-property-value prop4}}
        """.trimMargin()
        val actual = codeBlock.handlebars().apply(mapOf(
            "prop1" to """
            |hoge
            |fuga
            |piyo
            """.trimMargin(),
            "prop2" to "hoge=fuga",
            "prop3" to "hoge:fuga",
            "prop4" to "hoge\\fuga"
        ))
        val expect = """
        |app.prop1=hoge\
        |fuga\
        |piyo
        |app.prop2=hoge\=fuga
        |app.prop3=hoge\:fuga
        |app.prop4=hoge\\fuga
        """.trimMargin()
        assertEquals(expect, actual)
    }


    @Test
    fun test_line_continuation() {
        val codeBlock = """
        |app.prop1={{line-continuation this}}
        """.trimMargin()
        val actual = codeBlock.handlebars().apply("""
        |hoge
        |fuga
        |piyo
        """.trimMargin())
        val expect = """
        |app.prop1=hoge\
        |fuga\
        |piyo
        """.trimMargin()
        assertEquals(expect, actual)
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
            |{
            |  "H" : "hogehoge",
            |  "F" : false,
            |  "Z" : 0,
            |  "L" : [ "hoge", "fuga", "piyo" ]
            |}
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
    fun test_filter_not_empty() {
        val context = listOf(
            "hoge", "fuga", "piyo", null, ""
        )
        assertEquals(
            "hoge, fuga, piyo",
            """{{join (filter-not-empty this) ', ' }}""".handlebars().apply(context)
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
    fun test_find_helper() {
        val context = listOf(
            "hoge", "fuga", "piyo"
        )
        assertEquals(
            "hoge",
            """{{find this '(starts-with @it "h")'}}""".handlebars().apply(context)
        )
        assertEquals(
            "piyo",
            """{{find this '(starts-with @it "p")'}}""".handlebars().apply(context)
        )
        assertEquals(
            "",
            """{{find this '(starts-with @it "z")'}}""".handlebars().apply(context)
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

    @Test
    fun test_preprocess_template_string() {
        assertEquals(
          """String.format("hoge-%s-piyo!!", this.getFuga())""",
          "{{preprocess-template-string 'hoge-\${this.getFuga()}-piyo!!'}}".handlebars().apply("")
        )
        assertEquals(
            """log.info("hoge-{}-piyo!!", this.getFuga())""",
            "{{preprocess-template-string 'hoge-\${this.getFuga()}-piyo!!' function-name='log.info' placeholder='{}'}}".handlebars().apply("")
        )
        assertEquals(
            """this.getFuga()""",
            "{{preprocess-template-string '\${this.getFuga()}'}}".handlebars().apply("")
        )
        assertEquals(
            "\"HOGEHOGE\"",
            "{{preprocess-template-string 'HOGEHOGE'}}".handlebars().apply("")
        )
    }
}
