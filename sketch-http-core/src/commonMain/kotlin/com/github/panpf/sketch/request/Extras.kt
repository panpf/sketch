/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

import com.github.panpf.sketch.request.Extras.Entry
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.keyOrNull
import kotlin.jvm.JvmField

/**
 * A map of generic values that can be used to pass custom data to Fetcher and Decoder.
 */
class Extras private constructor(
    val entries: Map<String, Entry>
) : Iterable<Pair<String, Entry>>, Key {

    constructor() : this(emptyMap())

    /** Returns the number of parameters in this object. */
    val size: Int get() = entries.size

    override val key: String by lazy {
        val keys = entries
            .map { "${it.key}:${it.value.value}" }
            .sorted()
            .joinToString(separator = ",")
        "Extras($keys)"
    }

    val requestKey: String? by lazy {
        val keys = entries.asSequence()
            .filter { it.value.requestKey != null }
            .map { "${it.key}:${it.value.requestKey}" }
            .sorted()
            .joinToString(separator = ",")
        if (keys.isNotEmpty()) "Extras($keys)" else null
    }

    val cacheKey: String? by lazy {
        val keys = entries.asSequence()
            .filter { it.value.cacheKey != null }
            .map { "${it.key}:${it.value.cacheKey}" }
            .sorted()
            .joinToString(separator = ",")
        if (keys.isNotEmpty()) "Extras($keys)" else null
    }

    /** Returns the entry associated with [key] or null if [key] has no mapping. */
    fun entry(key: String): Entry? = entries[key]

    /** Returns the value associated with [key] or null if [key] has no mapping. */
    @Suppress("UNCHECKED_CAST")
    fun <T> value(key: String): T? = entry(key)?.value as T?

    /** Returns 'true' if this object has no parameters. */
    fun isEmpty(): Boolean = entries.isEmpty()

    fun keys(): Set<String> = entries.keys

    /** Returns a map of keys to values. */
    fun values(): Map<String, Any?> {
        return if (isEmpty()) {
            emptyMap()
        } else {
            entries.mapValues { it.value.value }
        }
    }

    /** Returns an [Iterator] over the entries in the [Extras]. */
    override operator fun iterator(): Iterator<Pair<String, Entry>> {
        return entries.map { (key, value) -> key to value }.iterator()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Extras && entries == other.entries
    }

    override fun hashCode() = entries.hashCode()

    override fun toString() = "Extras($entries)"

    /**
     * Create a new [Extras.Builder] based on the current [Extras].
     */
    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    /**
     * Create a new [Extras] based on the current [Extras].
     */
    fun newExtras(
        configBlock: (Builder.() -> Unit)? = null
    ): Extras = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    data class Entry constructor(
        val value: Any?,
        val cacheKey: String?,
        val requestKey: String?,
    )

    class Builder {

        private val entries: MutableMap<String, Entry>

        constructor() {
            entries = mutableMapOf()
        }

        constructor(extras: Extras) {
            entries = extras.entries.toMutableMap()
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
            cacheKey: String? = keyOrNull(value),
            requestKey: String? = keyOrNull(value),
        ) = apply {
            entries[key] = Entry(value, cacheKey, requestKey)
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

        /** Create a new [Extras] instance. */
        fun build() = Extras(entries.toMap())
    }

    companion object {
        @JvmField
        @Suppress("unused")
        val EMPTY = Extras()
    }
}

/** Returns the number of parameters in this object. */
fun Extras.count(): Int = size

/** Return true when the set contains elements. */
fun Extras.isNotEmpty(): Boolean = !isEmpty()

/** Returns the value associated with [key] or null if [key] has no mapping. */
operator fun Extras.get(key: String): Any? = value(key)

fun Extras?.merged(other: Extras?): Extras? {
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