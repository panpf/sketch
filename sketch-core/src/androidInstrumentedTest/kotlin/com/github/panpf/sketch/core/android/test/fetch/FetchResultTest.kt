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
package com.github.panpf.sketch.core.android.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.FetchResultImpl
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import okio.Path.Companion.toOkioPath
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FetchResultTest {

    @Test
    fun testCreateFunction() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "")

        FetchResult(
            FileDataSource(sketch, request, File("/sdcard/sample.jpeg").toOkioPath()),
            "image/jpeg"
        ).apply {
            Assert.assertTrue(this is FetchResultImpl)
        }
    }

    // TODO copy

    @Test
    fun testDataFrom() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "")

        FetchResult(
            FileDataSource(sketch, request, File("/sdcard/sample.jpeg").toOkioPath()),
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
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "")

        FetchResult(
            FileDataSource(sketch, request, File("/sdcard/sample.jpeg").toOkioPath()),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(
                "FetchResult(source=FileDataSource('/sdcard/sample.jpeg'),mimeType='image/jpeg')",
                this.toString()
            )
        }

        val data = byteArrayOf()
        FetchResult(
            ByteArrayDataSource(sketch, request, DataFrom.NETWORK, data),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(
                "FetchResult(source=ByteArrayDataSource(data=$data, from=NETWORK),mimeType='image/jpeg')",
                this.toString()
            )
        }
    }

    @Test
    fun testHeaderBytes() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "")

        val bytes = buildList {
            var number = 1
            repeat(101) {
                add((number++).toByte())
            }
        }.toByteArray()
        FetchResult(
            ByteArrayDataSource(sketch, request, MEMORY, bytes),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(
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
            ByteArrayDataSource(sketch, request, MEMORY, bytes1),
            "image/jpeg"
        ).apply {
            Assert.assertEquals(
                bytes1.toTypedArray().contentToString(),
                this.headerBytes.contentToString()
            )
        }
    }
}