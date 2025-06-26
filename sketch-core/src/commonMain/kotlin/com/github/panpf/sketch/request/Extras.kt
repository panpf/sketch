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
import com.github.panpf.sketch.util.Mergeable
import com.github.panpf.sketch.util.keyOrNull
import kotlin.jvm.JvmField

/**
 * A map of generic values that can be used to pass custom data to Fetcher and Decoder.
 *
 * @see com.github.panpf.sketch.core.common.test.request.ExtrasTest
 */
class Extras private constructor(
    val entries: Map<String, Entry>
) : Iterable<Pair<String, Entry>>, Key {

    constructor() : this(emptyMap())

    /**
     * Returns the number of parameters in this object.
     */
    val size: Int get() = entries.size

    override val key: String by lazy {
        val keys = entries
            .map { "${it.key}:${it.value.value}" }
            .sorted()
            .joinToString(separator = ",")
        "Extras($keys)"
    }

    /**
     * Returns a key that can be used to uniquely identify a request.
     */
    val requestKey: String? by lazy {
        val keys = entries.asSequence()
            .filter { it.value.requestKey != null }
            .map { "${it.key}:${it.value.requestKey}" }
            .sorted()
            .joinToString(separator = ",")
        if (keys.isNotEmpty()) "Extras($keys)" else null
    }

    /**
     * Returns a key that can be used to uniquely identify a request's cache.
     */
    val cacheKey: String? by lazy {
        val keys = entries.asSequence()
            .filter { it.value.cacheKey != null }
            .map { "${it.key}:${it.value.cacheKey}" }
            .sorted()
            .joinToString(separator = ",")
        if (keys.isNotEmpty()) "Extras($keys)" else null
    }

    /**
     * Returns the entry associated with [key] or null if [key] has no mapping.
     */
    fun entry(key: String): Entry? = entries[key]

    /**
     * Returns the value associated with [key] or null if [key] has no mapping.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> value(key: String): T? = entry(key)?.value as T?

    /**
     * Returns 'true' if this object has no parameters.
     */
    fun isEmpty(): Boolean = entries.isEmpty()

    /**
     * Get all keys
     */
    fun keys(): Set<String> = entries.keys

    /**
     * Returns a map of keys to values.
     */
    fun values(): Map<String, Any?> {
        return if (isEmpty()) {
            emptyMap()
        } else {
            entries.mapValues { it.value.value }
        }
    }

    /**
     * Returns an [Iterator] over the entries in the [Extras].
     */
    override operator fun iterator(): Iterator<Pair<String, Entry>> {
        return entries.map { (key, value) -> key to value }.iterator()
    }

    /**
     * Create a new [Extras.Builder] based on the current [Extras].
     */
    fun newBuilder(
        block: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        block?.invoke(this)
    }

    /**
     * Create a new [Extras] based on the current [Extras].
     */
    fun newExtras(
        block: (Builder.() -> Unit)? = null
    ): Extras = Builder(this).apply {
        block?.invoke(this)
    }.build()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Extras
        if (entries != other.entries) return false
        return true
    }

    override fun hashCode() = entries.hashCode()

    override fun toString() = "Extras($entries)"

    data class Entry(
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

/**
 * Returns the number of parameters in this object.
 *
 * @see com.github.panpf.sketch.core.common.test.request.ExtrasTest.testSizeAndCount
 */
fun Extras.count(): Int = size

/**
 * Return true when the set contains elements.
 *
 * @see com.github.panpf.sketch.core.common.test.request.ExtrasTest.testIsEmptyAndIsNotEmpty
 */
fun Extras.isNotEmpty(): Boolean = !isEmpty()

/**
 * Returns the value associated with [key] or null if [key] has no mapping.
 *
 * @see com.github.panpf.sketch.core.common.test.request.ExtrasTest.testValueAndGetAndCount
 */
operator fun Extras.get(key: String): Any? = value(key)

/**
 * Merge two Extras, based on the Extra on the left
 *
 * @see com.github.panpf.sketch.core.common.test.request.ExtrasTest.testMerged
 */
fun Extras?.merged(other: Extras?): Extras? {
    if (this == null || other == null) {
        return this ?: other
    }
    return this.newBuilder().apply {
        other.values().forEach {
            val existValue = this@merged.entry(it.key)?.value
            val otherValue = it.value
            if (existValue == null) {
                set(it.key, otherValue)
            } else if (existValue is Mergeable && otherValue is Mergeable) {
                val newValue = existValue.merge(otherValue)
                if (newValue !== existValue) {
                    set(it.key, newValue)
                }
            }
        }
    }.build()
}