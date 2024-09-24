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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.openSourceOrNull
import okio.buffer
import okio.use

/**
 * The result of [Fetcher.fetch]
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.FetchResultTest
 */
data class FetchResult constructor(
    /**
     * The data source of the fetched data
     */
    val dataSource: DataSource,
    /**
     * This mimeType is extracted from the uri and may not be accurate
     */
    val mimeType: String?
) {

    /**
     * The data source of the fetched data
     */
    val dataFrom: DataFrom
        get() = dataSource.dataFrom

    /**
     * 100 bytes of header
     */
    val headerBytes: ByteArray by lazy {
        val dataSource = dataSource
        val byteArray = ByteArray(100)
        val readLength = dataSource.openSourceOrNull()?.use {
            it.buffer().read(byteArray)
        } ?: -1
        if (readLength != -1) {
            if (readLength == byteArray.size) byteArray else byteArray.copyOf(readLength)
        } else {
            EMPTY_BYTE_ARRAY
        }
    }

    override fun toString(): String = "FetchResult(source=$dataSource, mimeType='$mimeType')"

    companion object {
        private val EMPTY_BYTE_ARRAY = ByteArray(0)
    }
}
