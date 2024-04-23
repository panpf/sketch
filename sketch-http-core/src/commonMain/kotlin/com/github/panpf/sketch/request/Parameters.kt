/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------------
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.Parameters.Entry
import kotlin.jvm.JvmField

/**
 * A map of generic values that can be used to pass custom data to Fetcher and Decoder.
 */
// TODO rename Extras
class Parameters private constructor(
    private val entries: Map<String, Entry>
) : Iterable<Pair<String, Entry>> {

    constructor() : this(emptyMap())

    /** Returns the number of parameters in this object. */
    val size: Int get() = entries.size

    val key: String? by lazy {
        val keys = entries
            .mapNotNull {
                it.value.value?.let { value -> "${it.key}:$value" }
            }.sorted()
            .joinToString(separator = ",")
        if (keys.isNotEmpty()) "Parameters($keys)" else null
    }

    val requestKey: String? by lazy {
        val keys = entries
            .mapNotNull {
                it.value
                    .takeIf { entry -> !entry.notJoinRequestKey }
                    ?.value?.let { value -> "${it.key}:$value" }
            }.sorted()
            .joinToString(separator = ",")
        if (keys.isNotEmpty()) "Parameters($keys)" else null
    }

    val cacheKey: String? by lazy {
        val keys = entries
            .mapNotNull {
                it.value.cacheKey?.let { cacheKey -> "${it.key}:$cacheKey" }
            }.sorted()
            .joinToString(separator = ",")
        if (keys.isNotEmpty()) "Parameters($keys)" else null
    }

    /** Returns the value associated with [key] or null if [key] has no mapping. */
    @Suppress("UNCHECKED_CAST")
    fun <T> value(key: String): T? = entries[key]?.value as T?

    /** Returns the cache key associated with [key] or null if [key] has no mapping. */
    fun cacheKey(key: String): String? = entries[key]?.cacheKey

    /** Returns the entry associated with [key] or null if [key] has no mapping. */
    fun entry(key: String): Entry? = entries[key]

    /** Returns 'true' if this object has no parameters. */
    fun isEmpty(): Boolean = entries.isEmpty()

    /** Returns a map of keys to values. */
    fun values(): Map<String, Any?> {
        return if (isEmpty()) {
            emptyMap()
        } else {
            entries.mapValues { it.value.value }
        }
    }

    /** Returns a map of keys to non-null cache keys. Keys with a null cache key are filtered. */
    fun cacheKeys(): Map<String, String> {
        return if (isEmpty()) {
            emptyMap()
        } else {
            entries.mapNotNull {
                it.value.cacheKey?.let { cacheKey ->
                    it.key to cacheKey
                }
            }.toMap()
        }
    }

    /** Returns an [Iterator] over the entries in the [Parameters]. */
    override operator fun iterator(): Iterator<Pair<String, Entry>> {
        return entries.map { (key, value) -> key to value }.iterator()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Parameters && entries == other.entries
    }

    override fun hashCode() = entries.hashCode()

    override fun toString() = "Parameters($entries)"

    /**
     * Create a new [Parameters.Builder] based on the current [Parameters].
     */
    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    /**
     * Create a new [Parameters] based on the current [Parameters].
     */
    fun newParameters(
        configBlock: (Builder.() -> Unit)? = null
    ): Parameters = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    data class Entry(
        val value: Any?,
        val cacheKey: String?,
        val notJoinRequestKey: Boolean = false,
    )

    class Builder {

        private val entries: MutableMap<String, Entry>

        constructor() {
            entries = mutableMapOf()
        }

        constructor(parameters: Parameters) {
            entries = parameters.entries.toMutableMap()
        }

        /**
         * Set a parameter.
         *
         * @param key The parameter's key.
         * @param value The parameter's value.
         * @param cacheKey The parameter's cache key.
         *  If not null, this value will be added to a request's cache key.
         */
        fun set(
            key: String,
            value: Any?,
            cacheKey: String? = value?.toString(),
            notJoinRequestKey: Boolean = false
        ) = apply {
            entries[key] = Entry(value, cacheKey, notJoinRequestKey)
        }

        /**
         * Remove a parameter.
         *
         * @param key The parameter's key.
         */
        fun remove(key: String) = apply {
            entries.remove(key)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> value(key: String): T? = entries[key]?.value as T?

        /** Create a new [Parameters] instance. */
        fun build() = Parameters(entries.toMap())
    }

    companion object {
        @JvmField
        @Suppress("unused")
        val EMPTY = Parameters()
    }
}

/** Returns the number of parameters in this object. */
fun Parameters.count(): Int = size

/** Return true when the set contains elements. */
fun Parameters.isNotEmpty(): Boolean = !isEmpty()

/** Returns the value associated with [key] or null if [key] has no mapping. */
operator fun Parameters.get(key: String): Any? = value(key)

fun Parameters?.merged(other: Parameters?): Parameters? {
    if (this == null || other == null) {
        return this ?: other
    }
    return this.newBuilder().apply {
        other.values().forEach {
            if (this@merged.entry(it.key) == null) {
                set(it.key, it.value)
            }
        }
    }.build()
}
