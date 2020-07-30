package laplacian.handlebars.helper

import java.io.IOException
import kotlin.random.Random
import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import laplacian.util.*
import laplacian.handlebars.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.Instant
import java.time.format.DateTimeFormatter

class FakeHelper : Helper<Any> {

    val KEY: String = "EVALUATED_VALUE"
    @Throws(IOException::class)
    override fun apply(context: Any?, options: Options): Any? {
        if (context == null) throw IllegalArgumentException(
            "The fake data category name must be specified. ex) {fake 'date-in-last-decade' 'yyyy-MM-dd'}"
        )
        val category = context.toString()
        val params = options.params
        return when(category) {
          "date-in-last-decade" -> dateInLastDecade(
              params.getOrNull(0)?.toString() ?: "uuuu-MM-dd"
          )
          else -> throw IllegalArgumentException(
            "Unknown fake data category: ${category}"
          )
        }
    }

    fun dateInLastDecade(format: String): String {
        val zoneId = ZoneId.systemDefault()
        val to = LocalDateTime.now()
        val from = to.withYear(to.year - 10)
        val epoch = Random.nextLong(
            from.atZone(zoneId).toEpochSecond(),
            to.atZone(zoneId).toEpochSecond()
        )
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), zoneId)
        return dateTime.format(DateTimeFormatter.ofPattern(format))
    }

    companion object {
        /**
         * A singleton instance of this helper.
         */
        val INSTANCE: Helper<Any> = FakeHelper()

        /**
         * The helper's name.
         */
        val NAME = "fake"
    }
}
