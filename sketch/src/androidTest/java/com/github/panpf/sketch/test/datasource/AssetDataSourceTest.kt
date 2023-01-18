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
package com.github.panpf.sketch.test.datasource

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class AssetDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndNewSketch()

        val request = LoadRequest(context, newAssetUri("sample.jpeg"))
        AssetDataSource(
            sketch = sketch,
            request = request,
            assetFileName = "sample.jpeg"
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals("sample.jpeg", this.assetFileName)
            Assert.assertEquals(DataFrom.LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndNewSketch()

        AssetDataSource(
            sketch = sketch,
            request = LoadRequest(context, newAssetUri("sample.jpeg")),
            assetFileName = "sample.jpeg"
        ).apply {
            newInputStream().close()
        }

        assertThrow(FileNotFoundException::class) {
            AssetDataSource(
                sketch = sketch,
                request = LoadRequest(context, newAssetUri("not_found.jpeg")),
                assetFileName = "not_found.jpeg"
            ).apply {
                newInputStream()
            }
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()

        AssetDataSource(
            sketch = sketch,
            request = LoadRequest(context, newAssetUri("sample.jpeg")),
            assetFileName = "sample.jpeg"
        ).apply {
            Assert.assertEquals(
                "AssetDataSource('sample.jpeg')",
                toString()
            )
        }

        AssetDataSource(
            sketch = sketch,
            request = LoadRequest(context, newAssetUri("not_found.jpeg")),
            assetFileName = "not_found.jpeg"
        ).apply {
            Assert.assertEquals("AssetDataSource('not_found.jpeg')", toString())
        }
    }
}