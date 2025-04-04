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

import okio.Path

/**
 * Parse this [String] into a [Uri].
 *
 * This method will not throw if the URI is malformed.
 *
 * @param separator The path separator used to separate URI path elements. By default, this
 *  will be '/' on UNIX systems and '\' on Windows systems.
 * @see com.github.panpf.sketch.core.common.test.util.UriTest.testToUri
 */
fun String.toUri(separator: String = Path.DIRECTORY_SEPARATOR): Uri {
    var data = this
    if (separator != "/") {
        data = data.replace(separator, "/")
    }
    val original = this
    return Uri(
        data = original,
        separator = separator,
        elementsLazy = lazyOf(parseUriElements(data = data, original = original))
    )
}

/**
 * Create a [Uri] from parts without parsing.
 *
 * @see toUri
 */
fun buildUri(
    scheme: String? = null,
    authority: String? = null,
    path: String? = null,
    query: String? = null,
    fragment: String? = null,
    separator: String = Path.DIRECTORY_SEPARATOR,
): Uri {
    require(scheme != null || authority != null || path != null || query != null || fragment != null) {
        "At least one of scheme, authority, path, query, or fragment must be non-null."
    }

    return Uri(
        data = buildData(scheme, authority, path, query, fragment),
        separator = separator,
        elements = Uri.Elements(
            scheme = scheme,
            authority = authority,
            path = path,
            query = query,
            fragment = fragment,
        )
    )
}

/**
 * A uniform resource locator.
 *
 * @see com.github.panpf.sketch.core.common.test.util.UriTest
 */
class Uri internal constructor(
    private val data: String,
    val separator: String,
    elementsLazy: Lazy<Elements>
) {

    private val elements: Elements by elementsLazy

    constructor(data: String, separator: String, elements: Elements) : this(
        data = data,
        separator = separator,
        elementsLazy = lazyOf(elements)
    )

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
            var index = -1
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

    /**
     * Returns the URI's [Uri.path] formatted according to the URI's native [Uri.separator].
     */
    val filePath: String? by lazy {
        val pathSegments = pathSegments
        if (pathSegments.isEmpty()) {
            null
        } else {
            val prefix = if (path!!.startsWith(separator)) separator else ""
            pathSegments.joinToString(prefix = prefix, separator = separator)
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

private fun buildData(
    scheme: String?,
    authority: String?,
    path: String?,
    query: String?,
    fragment: String?,
) = buildString {
    if (scheme != null) {
        append(scheme)
        append(':')
    }
    if (authority != null) {
        append("//")
        append(authority)
    }
    if (path != null) {
        append(path)
    }
    if (query != null) {
        append('?')
        append(query)
    }
    if (fragment != null) {
        append('#')
        append(fragment)
    }
}

private fun parseUriElements(
    data: String,
    original: String,
): Uri.Elements {
    var openScheme = true
    var schemeEndIndex = -1
    var authorityStartIndex = -1
    var pathStartIndex = -1
    var queryStartIndex = -1
    var fragmentStartIndex = -1
    var index = 0

    while (index < data.length) {
        when (data[index]) {
            ':' -> {
                if (openScheme &&
                    queryStartIndex == -1 &&
                    fragmentStartIndex == -1
                ) {
                    if (index + 2 < original.length &&
                        original[index + 1] == '/' &&
                        original[index + 2] == '/'
                    ) {
                        // Standard URI with an authority (e.g. "file:///path/image.jpg").
                        openScheme = false
                        schemeEndIndex = index
                        authorityStartIndex = index + 3
                        index += 2
                    } else if (data == original) {
                        // Special URI that has no authority (e.g. "file:/path/image.jpg").
                        schemeEndIndex = index
                        authorityStartIndex = index + 1
                        pathStartIndex = index + 1
                        index += 1
                    }
                }
            }

            '/' -> {
                if (pathStartIndex == -1 &&
                    queryStartIndex == -1 &&
                    fragmentStartIndex == -1
                ) {
                    openScheme = false
                    pathStartIndex = if (authorityStartIndex == -1) 0 else index
                }
            }

            '?' -> {
                if (queryStartIndex == -1 &&
                    fragmentStartIndex == -1
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
        scheme = data.substring(0, schemeEndIndex)

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

    val maxLength = maxOf(
        0,
        maxOf(
            scheme.length,
            authority.length,
            maxOf(
                path.length,
                query.length,
                fragment.length,
            ),
        ) - 2,
    )
    val bytes = ByteArray(maxLength)
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
    val length = length
    val searchLength = maxOf(0, length - 2)

    while (true) {
        if (index >= searchLength) {
            if (index == size) {
                // Fast path: the string doesn't have any encoded characters.
                return this
            } else if (index >= length) {
                // Slow path: decode the byte array.
                return bytes.decodeToString(endIndex = size)
            }
        } else if (get(index) == '%') {
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
}

private val String?.length: Int
    get() = this?.length ?: 0