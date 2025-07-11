/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.http

import com.github.panpf.sketch.util.Mergeable

/**
 * Create a new [HttpHeaders]
 *
 * @see com.github.panpf.sketch.http.core.common.test.http.HttpHeadersTest.testHttpHeaders
 */
fun HttpHeaders(block: HttpHeaders.Builder.() -> Unit): HttpHeaders {
    return HttpHeaders.Builder().apply(block).build()
}

/**
 * Set headers for http requests
 *
 * @see com.github.panpf.sketch.http.core.common.test.http.HttpHeadersTest
 */
class HttpHeaders(
    val addList: List<Pair<String, String>>,
    val setList: List<Pair<String, String>>,
) : Mergeable {

    constructor() : this(emptyList(), emptyList())

    val size: Int = addList.size + setList.size

    val addSize: Int = addList.size

    val setSize: Int = setList.size

    fun getAdd(key: String): List<String>? {
        return addList.filter { it.first == key }.map { it.second }.takeIf { it.isNotEmpty() }
    }

    fun getSet(key: String): String? {
        return setList.find { it.first == key }?.second
    }

    fun isEmpty(): Boolean = addList.isEmpty() && setList.isEmpty()

    /**
     * Create a new [HttpHeaders.Builder] based on the current [HttpHeaders].
     *
     * You can extend it with a trailing lambda function [block]
     */
    fun newBuilder(
        block: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        block?.invoke(this)
    }

    /**
     * Create a new [HttpHeaders] based on the current [HttpHeaders].
     *
     * You can extend it with a trailing lambda function [block]
     */
    fun newHttpHeaders(
        block: (Builder.() -> Unit)? = null
    ): HttpHeaders = Builder(this).apply {
        block?.invoke(this)
    }.build()

    override fun merge(other: Mergeable): Mergeable {
        if (other !is HttpHeaders) return this
        return this.merged(other)!!
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as HttpHeaders
        if (addList != other.addList) return false
        if (setList != other.setList) return false
        return true
    }

    override fun hashCode(): Int {
        var result = addList.hashCode()
        result = 31 * result + setList.hashCode()
        return result
    }

    override fun toString(): String {
        val setListString = setList.joinToString(prefix = "[", postfix = "]", separator = ",") {
            "${it.first}:${it.second}"
        }
        val addListString = addList.joinToString(prefix = "[", postfix = "]", separator = ",") {
            "${it.first}:${it.second}"
        }
        return "HttpHeaders(sets=$setListString,adds=$addListString)"
    }

    class Builder {

        private val addList = mutableListOf<Pair<String, String>>()
        private val setList = mutableListOf<Pair<String, String>>()

        constructor()

        constructor(headers: HttpHeaders) {
            this.addList.addAll(headers.addList)
            this.setList.addAll(headers.setList)
        }

        fun add(name: String, value: String): Builder = apply {
            setList.removeAll {
                it.first == name
            }
            addList.add(name to value)
        }

        fun set(name: String, value: String): Builder = apply {
            removeAll(name)
            setList.add(name to value)
        }

        fun removeAll(name: String): Builder = apply {
            addList.removeAll {
                it.first == name
            }
            setList.removeAll {
                it.first == name
            }
        }

        fun build(): HttpHeaders = HttpHeaders(addList.toList(), setList.toList())
    }
}

/**
 * Return true when the set contains elements.
 *
 * @see com.github.panpf.sketch.http.core.common.test.http.HttpHeadersTest.testIsEmptyAndIsNotEmpty
 */
fun HttpHeaders.isNotEmpty(): Boolean = !isEmpty()

/**
 * Merge two [HttpHeaders] into one [HttpHeaders]
 *
 * @see com.github.panpf.sketch.http.core.common.test.http.HttpHeadersTest.testMerged
 */
fun HttpHeaders?.merged(other: HttpHeaders?): HttpHeaders? {
    if (this == null || other == null) {
        return this ?: other
    }
    return this.newBuilder().apply {
        other.setList.forEach {
            if (this@merged.getSet(it.first) == null) {
                set(it.first, it.second)
            }
        }
        other.addList.forEach {
            add(it.first, it.second)
        }
    }.build()
}
