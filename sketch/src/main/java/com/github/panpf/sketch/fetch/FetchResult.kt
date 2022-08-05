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
package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.UnavailableDataSource
import com.github.panpf.sketch.fetch.internal.HeaderBytes

fun FetchResult(dataSource: DataSource, mimeType: String?): FetchResult =
    DefaultFetchResult(dataSource, mimeType)

/**
 * The result of [Fetcher.fetch]
 */
interface FetchResult {

    val dataSource: DataSource

    val mimeType: String?

    val dataFrom: DataFrom
        get() = dataSource.dataFrom

    /**
     * 1024 bytes of header
     */
    val headerBytes: HeaderBytes
}

open class DefaultFetchResult constructor(
    override val dataSource: DataSource, override val mimeType: String?
) : FetchResult {

    override val headerBytes: HeaderBytes by lazy {
        if (dataSource !is UnavailableDataSource) {
            val byteArray = ByteArray(1024)
            val readLength = dataSource.newInputStream().use {
                it.read(byteArray)
            }
            HeaderBytes(
                if (readLength == byteArray.size) {
                    byteArray
                } else {
                    byteArray.copyOf(readLength)
                }
            )
        } else {
            HeaderBytes(ByteArray(0))
        }
    }

    override fun toString(): String = "FetchResult(source=$dataSource,mimeType='$mimeType')"
}
