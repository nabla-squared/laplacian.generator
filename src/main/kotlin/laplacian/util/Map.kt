package laplacian.util

import com.github.jknack.handlebars.Context
import org.yaml.snakeyaml.Yaml

inline fun <reified T> Map<String, Any?>.getOrNull(key: String): T? {
    val v = this[key]
    return when {
        (v == null) -> null
        (v is T) -> v
        else -> throw IllegalStateException(
            "'$key' has a value but its type is not expected one but is ${v.javaClass.canonicalName}"
        )
    }
}

inline fun <reified T> Map<String, Any?>.getOrThrow(key: String, defaultValueProvider: () -> T?): T {
    val v = this.getOrElse(key) {
        val defaultValue = defaultValueProvider()
        if (defaultValue != null) return defaultValue
        val keys = this.keys.joinToString(", ")
        throw IllegalArgumentException(
            "'$key' is not registered in this map and default value was not given. possible keys: $keys"
        )
    }
    if (v is T) return v
    throw IllegalStateException(
        "'$key' has a value but its type is not expected one but is: ${v!!.javaClass.canonicalName}"
    )
}

inline fun <reified T> Map<String, Any?>.getOrThrow(key: String, defaultValue: T? = null): T {
    return this.getOrThrow(key) { defaultValue }
}

fun <T> Map<String, Any?>.getList(key: String, defaultList: List<T>? = null): List<T> {
    if (defaultList == null) return this.getOrThrow(key)
    if (!this.containsKey(key)) return defaultList
    val v = this[key]
    if (v is List<*>) return v as List<T>
    throw IllegalStateException(
        "'$key' has a value but its type is not list but is: ${v!!.javaClass.canonicalName}"
    )
}

fun Map<String, Any?>.getString(key: String): String {
    return this.getOrThrow(key)
}

fun <T> Map<String, Any?>.retrieve(path: String): T? = TemplateWrapper.createContext(this).get(path) as T?


fun Map<String, Any?>.normalizeCamelcase(): Map<String, Any?> = CamelcaseNormalizedMap(this).withDefault { null }


class CamelcaseNormalizedMap(private val map:Map<String, Any?>): Map<String, Any?> by map {
    override fun get(key: String) = map[key.lowerUnderscorize()]
}

fun Map<String, Any?>.toContext(): Context {
    return TemplateWrapper.createContext(this)
}

typealias Record = Map<String, Any?>
typealias RecordList = List<Record>
typealias Model = Map<String, RecordList>

fun List<Record>.mergeWithKeys(vararg keys: String): List<Record> {
    if (keys.isEmpty()) return this
    val recordsByKey = mutableMapOf<String, Record>()
    this.forEach { record ->
        val key = Yaml().dump(keys.map { record.getOrDefault(it, "") })
        val existing = recordsByKey[key]
        recordsByKey[key] = if (existing == null) record
                            else mergeObjectGraph(existing, record, keys.toList()) as Record
    }
    return recordsByKey.values.toList()
}

fun mergeObjectGraph(one: Any, another: Any, ignoringKeys: List<String> = emptyList(), path: String = ""): Any {
    if (one is Map<*, *> && another is Map<*, *>) {
        return one + another.map{ entry ->
            val k = entry.key
            val anotherValue = entry.value
            val value = one[k]
            when {
                (k in ignoringKeys) -> k to value
                (value == null) -> k to anotherValue
                (anotherValue == null) -> k to value
                else -> k to mergeObjectGraph(
                    value,
                    anotherValue,
                    ignoringKeys,
                    path + (if (path.isEmpty()) "" else ".") + k
                )
            }
        }
    }
    if (one is List<*> && another is List<*>) {
        return one + another
    }
    else {
        throw IllegalArgumentException(
            " the following model items at '${if (path.isEmpty()) "root" else path}' conflict" +
            ": $one and $another"
        )
    }
}
