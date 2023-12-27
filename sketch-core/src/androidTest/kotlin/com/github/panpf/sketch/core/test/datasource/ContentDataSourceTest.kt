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
package com.github.panpf.sketch.core.test.datasource

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.core.test.getTestContextAndNewSketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class ContentDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndNewSketch()
        val contentUri = AssetDataSource(
            sketch = sketch,
            request = LoadRequest(context, AssetImages.jpeg.uri),
            assetFileName = AssetImages.jpeg.fileName
        ).getFile().let { Uri.fromFile(it) }
        val request = LoadRequest(context, contentUri.toString())
        ContentDataSource(
            sketch = sketch,
            request = request,
            contentUri = contentUri,
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals(contentUri, this.contentUri)
            Assert.assertEquals(DataFrom.LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndNewSketch()
        val contentUri = AssetDataSource(
            sketch = sketch,
            request = LoadRequest(context, AssetImages.jpeg.uri),
            assetFileName = AssetImages.jpeg.fileName
        ).getFile().let { Uri.fromFile(it) }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(context, contentUri.toString()),
            contentUri = contentUri,
        ).apply {
            newInputStream().close()
        }

        assertThrow(FileNotFoundException::class) {
            val errorContentUri = runBlocking {
                Uri.fromFile(File("/sdcard/error.jpeg"))
            }
            ContentDataSource(
                sketch = sketch,
                request = LoadRequest(context, errorContentUri.toString()),
                contentUri = errorContentUri,
            ).apply {
                newInputStream()
            }
        }
    }

    @Test
    fun testFile() {
        val (context, sketch) = getTestContextAndNewSketch()
        val contentUri = AssetDataSource(
            sketch = sketch,
            request = LoadRequest(context, AssetImages.jpeg.uri),
            assetFileName = AssetImages.jpeg.fileName
        ).getFile().let { Uri.fromFile(it) }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(context, contentUri.toString()),
            contentUri = contentUri,
        ).apply {
            val file = getFile()
            Assert.assertEquals("01d95711e2e30d06b88b93f82e3e1bde.0", file.name)
        }

        val errorContentUri = Uri.fromFile(File("/sdcard/error.jpeg"))
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(context, errorContentUri.toString()),
            contentUri = errorContentUri,
        ).apply {
            val file = getFile()
            Assert.assertEquals("/sdcard/error.jpeg", file.path)
        }

        assertThrow(FileNotFoundException::class) {
            val errorContentUri1 = Uri.parse("content://fake/fake.jpeg")
            ContentDataSource(
                sketch = sketch,
                request = LoadRequest(context, errorContentUri1.toString()),
                contentUri = errorContentUri1,
            ).getFile()
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()
        val contentUri = AssetDataSource(
            sketch = sketch,
            request = LoadRequest(context, AssetImages.jpeg.uri),
            assetFileName = AssetImages.jpeg.fileName
        ).getFile().let { Uri.fromFile(it) }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(context, contentUri.toString()),
            contentUri = contentUri,
        ).apply {
            Assert.assertEquals(
                "ContentDataSource('$contentUri')",
                toString()
            )
        }

        val errorContentUri = runBlocking {
            Uri.fromFile(File("/sdcard/error.jpeg"))
        }
        ContentDataSource(
            sketch = sketch,
            request = LoadRequest(context, errorContentUri.toString()),
            contentUri = errorContentUri,
        ).apply {
            Assert.assertEquals(
                "ContentDataSource('file:///sdcard/error.jpeg')",
                toString()
            )
        }
    }
}
