@file:JvmName("-Parameters")
@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.github.panpf.sketch.request

import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Parameters.Entry

/** A map of generic values that can be used to pass custom data to [Fetcher]s and [BitmapDecoder]s. */
class Parameters private constructor(
    private val map: Map<String, Entry>
) : Iterable<Pair<String, Entry>> {

    constructor() : this(emptyMap())

    /** Returns the number of parameters in this object. */
    val size: Int @JvmName("size") get() = map.size

    /** Returns the value associated with [key] or null if [key] has no mapping. */
    @Suppress("UNCHECKED_CAST")
    fun <T> value(key: String): T? = map[key]?.value as T?

    /** Returns the cache key associated with [key] or null if [key] has no mapping. */
    fun cacheKey(key: String): String? = map[key]?.cacheKey

    /** Returns the entry associated with [key] or null if [key] has no mapping. */
    fun entry(key: String): Entry? = map[key]

    /** Returns 'true' if this object has no parameters. */
    fun isEmpty(): Boolean = map.isEmpty()

    /** Returns a map of keys to values. */
    fun values(): Map<String, Any?> {
        return if (isEmpty()) {
            emptyMap()
        } else {
            map.mapValues { it.value.value }
        }
    }

    /** Returns a map of keys to non-null cache keys. Keys with a null cache key are filtered. */
    fun cacheKeys(): Map<String, String> {
        return if (isEmpty()) {
            emptyMap()
        } else {
            map.mapNotNull {
                it.value.cacheKey?.let { cacheKey ->
                    it.key to cacheKey
                }
            }.toMap()
        }
    }

    val key: String? by lazy {
        val keys = map.mapNotNull {
            it.value.value?.let { value ->
                "${it.key}:$value"
            }
        }.sorted().joinToString(separator = ",")
        if(keys.isNotEmpty()) {
            "Parameters($keys)"
        } else {
            null
        }
    }

    val cacheKey: String? by lazy {
        val keys = map.mapNotNull {
            it.value.cacheKey?.let { cacheKey ->
                "${it.key}:$cacheKey"
            }
        }.sorted().joinToString(separator = ",")
        if(keys.isNotEmpty()) {
            "Parameters($keys)"
        } else {
            null
        }
    }

    /** Returns an [Iterator] over the entries in the [Parameters]. */
    override operator fun iterator(): Iterator<Pair<String, Entry>> {
        return map.map { (key, value) -> key to value }.iterator()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Parameters && map == other.map
    }

    override fun hashCode() = map.hashCode()

    override fun toString() = "Parameters(map=$map)"

    fun newBuilder() = Builder(this)

    data class Entry(
        val value: Any?,
        val cacheKey: String?,
    )

    class Builder {

        private val map: MutableMap<String, Entry>

        constructor() {
            map = mutableMapOf()
        }

        constructor(parameters: Parameters) {
            map = parameters.map.toMutableMap()
        }

        /**
         * Set a parameter.
         *
         * @param key The parameter's key.
         * @param value The parameter's value.
         * @param cacheKey The parameter's cache key.
         *  If not null, this value will be added to a request's cache key.
         */
        @JvmOverloads
        fun set(key: String, value: Any?, cacheKey: String? = value?.toString()) = apply {
            map[key] = Entry(value, cacheKey)
        }

        /**
         * Remove a parameter.
         *
         * @param key The parameter's key.
         */
        fun remove(key: String) = apply {
            map.remove(key)
        }

        fun exist(key: String): Boolean = map[key] != null

        /** Create a new [Parameters] instance. */
        fun build() = Parameters(map.toMap())
    }

    companion object {
        @JvmField
        val EMPTY = Parameters()
    }
}

/** Returns the number of parameters in this object. */
inline fun Parameters.count(): Int = size

/** Return true when the set contains elements. */
inline fun Parameters.isNotEmpty(): Boolean = !isEmpty()

/** Returns the value associated with [key] or null if [key] has no mapping. */
inline operator fun Parameters.get(key: String): Any? = value(key)
