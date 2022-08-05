/*
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
package com.github.panpf.sketch.http

import java.util.LinkedList

/**
 * Set headers for http requests
 */
class HttpHeaders(
    val addList: List<Pair<String, String>>,
    val setList: List<Pair<String, String>>,
) {

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
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    /**
     * Create a new [HttpHeaders] based on the current [HttpHeaders].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newHttpHeaders(
        configBlock: (Builder.() -> Unit)? = null
    ): HttpHeaders = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    override fun toString(): String {
        val setListString = setList.joinToString(prefix = "[", postfix = "]", separator = ",") {
            "${it.first}:${it.second}"
        }
        val addListString = addList.joinToString(prefix = "[", postfix = "]", separator = ",") {
            "${it.first}:${it.second}"
        }
        return "HttpHeaders(sets=$setListString,adds=$addListString)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HttpHeaders) return false
        if (addList != other.addList) return false
        if (setList != other.setList) return false
        return true
    }

    override fun hashCode(): Int {
        var result = addList.hashCode()
        result = 31 * result + setList.hashCode()
        return result
    }


    class Builder {

        private val addList = LinkedList<Pair<String, String>>()
        private val setList = LinkedList<Pair<String, String>>()

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

/** Return true when the set contains elements. */
fun HttpHeaders.isNotEmpty(): Boolean = !isEmpty()

fun HttpHeaders?.merged(other: HttpHeaders?): HttpHeaders? =
    if (this != null) {
        if (other != null) {
            this.newBuilder().apply {
                other.setList.forEach {
                    if (this@merged.getSet(it.first) == null) {
                        set(it.first, it.second)
                    }
                }
                other.addList.forEach {
                    add(it.first, it.second)
                }
            }.build()
        } else {
            this
        }
    } else {
        other
    }