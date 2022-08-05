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
package com.github.panpf.sketch.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.fetch.DefaultFetchResult
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.utils.TestUnavailableDataSource
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FetchResultTest {

    @Test
    fun testCreateFunction() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, "")

        FetchResult(
            FileDataSource(sketch, request, File("/sdcard/sample.jpeg")),
            "image/jpeg"
        ).apply {
            Assert.assertTrue(this is DefaultFetchResult)
        }
    }

    @Test
    fun testDataFrom() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, "")

        FetchResult(
            FileDataSource(sketch, request, File("/sdcard/sample.jpeg")),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }

        FetchResult(
            ByteArrayDataSource(sketch, request, DataFrom.NETWORK, byteArrayOf()),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(DataFrom.NETWORK, dataFrom)
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, "")

        FetchResult(
            FileDataSource(sketch, request, File("/sdcard/sample.jpeg")),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(
                "FetchResult(source=FileDataSource(file='/sdcard/sample.jpeg'),mimeType='image/jpeg')",
                this.toString()
            )
        }

        FetchResult(
            ByteArrayDataSource(sketch, request, DataFrom.NETWORK, byteArrayOf()),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(
                "FetchResult(source=ByteArrayDataSource(from=NETWORK,length=0),mimeType='image/jpeg')",
                this.toString()
            )
        }
    }

    @Test
    fun testHeaderBytes() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, "")

        FetchResult(
            TestUnavailableDataSource(sketch, request, MEMORY),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(
                byteArrayOf().contentToString(),
                this.headerBytes.bytes.contentToString()
            )
        }

        val bytes = buildList {
            var number = 1
            repeat(1025) {
                add((number++).toByte())
            }
        }.toByteArray()
        FetchResult(
            ByteArrayDataSource(sketch, request, MEMORY, bytes),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(
                bytes.take(1024).toTypedArray().contentToString(),
                this.headerBytes.bytes.contentToString()
            )
        }

        val bytes1 = buildList {
            var number = 1
            repeat(1023) {
                add((number++).toByte())
            }
        }.toByteArray()
        FetchResult(
            ByteArrayDataSource(sketch, request, MEMORY, bytes1),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(
                bytes1.toTypedArray().contentToString(),
                this.headerBytes.bytes.contentToString()
            )
        }
    }
}