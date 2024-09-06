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

package com.github.panpf.sketch.core.common.test.fetch

import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.FetchResultImpl
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.source.FileDataSource
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FetchResultTest {

    @Test
    fun testCreateFunction() {
        FetchResult(
            FileDataSource("/sdcard/sample.jpeg".toPath()),
            "image/jpeg"
        ).apply {
            assertTrue(this is FetchResultImpl)
        }
    }

    @Test
    fun testCopy() {
        // TODO copy
    }

    @Test
    fun testDataFrom() {
        FetchResult(
            FileDataSource("/sdcard/sample.jpeg".toPath()),
            "image/jpeg"
        ).apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        FetchResult(
            ByteArrayDataSource(byteArrayOf(), DataFrom.NETWORK),
            "image/jpeg"
        ).apply {
            assertEquals(DataFrom.NETWORK, dataFrom)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val factory1 = FetchResult(
            dataSource = FileDataSource(path = "/sdcard/sample.jpeg".toPath()),
            mimeType = "image/jpeg"
        )
        val factory11 = FetchResult(
            dataSource = FileDataSource(path = "/sdcard/sample.jpeg".toPath()),
            mimeType = "image/jpeg"
        )
        val factory2 = FetchResult(
            dataSource = FileDataSource(path = "/sdcard/sample.png".toPath()),
            mimeType = "image/jpeg"
        )
        val factory3 = FetchResult(
            dataSource = FileDataSource(path = "/sdcard/sample.jpeg".toPath()),
            mimeType = "image/png"
        )

        assertEquals(expected = factory1, actual = factory11)
        assertNotEquals(illegal = factory1, actual = factory2)
        assertNotEquals(illegal = factory1, actual = factory3)
        assertNotEquals(illegal = factory2, actual = factory3)
        assertNotEquals(illegal = factory1, actual = null as Any?)
        assertNotEquals(illegal = factory1, actual = Any())

        assertEquals(expected = factory1.hashCode(), actual = factory11.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory2.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory3.hashCode())
        assertNotEquals(illegal = factory2.hashCode(), actual = factory3.hashCode())
    }

    @Test
    fun testToString() {
        FetchResult(
            FileDataSource("/sdcard/sample.jpeg".toPath()),
            "image/jpeg"
        ).apply {
            assertEquals(
                "FetchResult(source=FileDataSource(path='/sdcard/sample.jpeg', from=LOCAL), mimeType='image/jpeg')",
                this.toString()
            )
        }

        val data = byteArrayOf()
        FetchResult(
            ByteArrayDataSource(data, DataFrom.NETWORK),
            "image/jpeg"
        ).apply {
            assertEquals(
                "FetchResult(source=ByteArrayDataSource(data=$data, from=NETWORK), mimeType='image/jpeg')",
                this.toString()
            )
        }
    }

    @Test
    fun testHeaderBytes() {
        val bytes = buildList {
            var number = 1
            repeat(101) {
                add((number++).toByte())
            }
        }.toByteArray()
        FetchResult(
            ByteArrayDataSource(bytes, MEMORY),
            "image/jpeg"
        ).apply {
            assertEquals(
                bytes.take(100).toTypedArray().contentToString(),
                this.headerBytes.contentToString()
            )
        }

        val bytes1 = buildList {
            var number = 1
            repeat(99) {
                add((number++).toByte())
            }
        }.toByteArray()
        FetchResult(
            ByteArrayDataSource(bytes1, MEMORY),
            "image/jpeg"
        ).apply {
            assertEquals(
                bytes1.toTypedArray().contentToString(),
                this.headerBytes.contentToString()
            )
        }
    }
}