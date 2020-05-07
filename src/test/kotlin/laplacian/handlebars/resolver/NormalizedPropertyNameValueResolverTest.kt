package laplacian.handlebars.resolver
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertAll
import com.github.jknack.handlebars.ValueResolver

class NormalizedPropertyNameValueResolverTest {
    data class Context(
        private val _strValue: String,
        private val _intValue: Int
    ) {
        fun getStrValue() = _strValue
        fun getIntValue() = _intValue
    }
    @Test
    fun test_resolve_property() {
        val resolver = NormalizedPropertyNameValueResolver()
        val ctx = Context("hoge", 42)
        assertAll({
            assertEquals(
                ValueResolver.UNRESOLVED, resolver.resolve(ctx, "unknown")
            )
        }, {
            assertEquals(
                "hoge", resolver.resolve(ctx, "str-value")
            )
        }, {
            assertEquals(
                42, resolver.resolve(ctx, "INT VALUE")
            )
        })
    }
}
