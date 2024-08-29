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

package com.github.panpf.sketch.util

/**
 * Parse this [String] into a [Uri].
 *
 * This method will not throw if the URI is malformed.
 */
fun String.toUri(): Uri = Uri(this)

/**
 * A uniform resource locator.
 */
class Uri internal constructor(private val data: String) {

    private val elements: Elements by lazy { parseUri(data) }

    val scheme: String?
        get() = elements.scheme
    val authority: String?
        get() = elements.authority
    val path: String?
        get() = elements.path
    val query: String?
        get() = elements.query
    val fragment: String?
        get() = elements.fragment

    /**
     * Return the separate segments of the [Uri.path].
     */
    val pathSegments: List<String> by lazy {
        val path = path
        if (path != null) {
            val segments = mutableListOf<String>()
            var index = 0
            while (index < path.length) {
                val startIndex = index + 1
                index = path.indexOf('/', startIndex)
                if (index == -1) {
                    index = path.length
                }

                val segment = path.substring(startIndex, index)
                if (segment.isNotEmpty()) {
                    segments += segment
                }
            }
            segments
        } else {
            emptyList()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Uri
        if (data != other.data) return false
        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        return data
    }

    class Elements(
        val scheme: String?,
        val authority: String?,
        val path: String?,
        val query: String?,
        val fragment: String?,
    )
}

private fun parseUri(data: String): Uri.Elements {
    var authorityStartIndex = -1
    var pathStartIndex = -1
    var queryStartIndex = -1
    var fragmentStartIndex = -1
    var index = 0

    while (index < data.length) {
        when (data[index]) {
            ':' -> {
                if (queryStartIndex == -1 &&
                    fragmentStartIndex == -1 &&
                    pathStartIndex == -1 &&
                    authorityStartIndex == -1 &&
                    index + 2 < data.length &&
                    data[index + 1] == '/' &&
                    data[index + 2] == '/'
                ) {
                    authorityStartIndex = index + 3
                    index += 2
                }
            }

            '/' -> {
                if (queryStartIndex == -1 &&
                    fragmentStartIndex == -1 &&
                    pathStartIndex == -1
                ) {
                    pathStartIndex = index
                }
            }

            '?' -> {
                if (fragmentStartIndex == -1 &&
                    queryStartIndex == -1
                ) {
                    queryStartIndex = index + 1
                }
            }

            '#' -> {
                if (fragmentStartIndex == -1) {
                    fragmentStartIndex = index + 1
                }
            }
        }
        index++
    }

    var scheme: String? = null
    var authority: String? = null
    var path: String? = null
    var query: String? = null
    var fragment: String? = null

    val queryEndIndex = minOf(
        if (fragmentStartIndex == -1) Int.MAX_VALUE else fragmentStartIndex - 1,
        data.length,
    )
    val pathEndIndex = minOf(
        if (queryStartIndex == -1) Int.MAX_VALUE else queryStartIndex - 1,
        queryEndIndex,
    )

    if (authorityStartIndex != -1) {
        scheme = data.substring(0, authorityStartIndex - 3)

        val authorityEndIndex = minOf(
            if (pathStartIndex == -1) Int.MAX_VALUE else pathStartIndex,
            pathEndIndex,
        )
        authority = data.substring(authorityStartIndex, authorityEndIndex)
    }

    if (pathStartIndex != -1) {
        path = data.substring(pathStartIndex, pathEndIndex)
    }
    if (queryStartIndex != -1) {
        query = data.substring(queryStartIndex, queryEndIndex)
    }
    if (fragmentStartIndex != -1) {
        fragment = data.substring(fragmentStartIndex, data.length)
    }

    val size = maxOf(
        scheme?.length ?: 0,
        authority?.length ?: 0,
        maxOf(
            path?.length ?: 0,
            query?.length ?: 0,
            fragment?.length ?: 0,
        ),
    )
    val bytes = ByteArray(size)
    return Uri.Elements(
        scheme = scheme?.percentDecode(bytes),
        authority = authority?.percentDecode(bytes),
        path = path?.percentDecode(bytes),
        query = query?.percentDecode(bytes),
        fragment = fragment?.percentDecode(bytes),
    )
}

private fun String.percentDecode(bytes: ByteArray): String {
    var size = 0
    var index = 0

    while (index < length) {
        if (get(index) == '%' && index + 2 < length) {
            try {
                val hex = substring(index + 1, index + 3)
                bytes[size] = hex.toInt(16).toByte()
                size++
                index += 3
                continue
            } catch (_: NumberFormatException) {
            }
        }

        bytes[size] = get(index).code.toByte()
        size++
        index++
    }

    if (size == length) {
        // Fast path: the string doesn't have any encoded characters.
        return this
    } else {
        // Slow path: decode the byte array.
        return bytes.decodeToString(endIndex = size)
    }
}