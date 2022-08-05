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
package com.github.panpf.sketch.gif.koral.test

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.DataFrom.NETWORK
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.datasource.UnavailableDataSource
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import pl.droidsonroids.gif.GifInfoHandleHelper
import pl.droidsonroids.gif.GifOptions
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class GifInfoHandlerHelperTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        val snapshot = runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = LoadRequest(context, newAssetUri("sample_anim.gif")),
                assetFileName = "sample_anim.gif"
            ).file()
            sketch.resultCache[newAssetUri("sample_anim.gif") + "_data_source"]!!
        }

        GifInfoHandleHelper(
            ByteArrayDataSource(
                sketch = sketch,
                request = LoadRequest(context, "http://sample.com/sample.gif"),
                dataFrom = NETWORK,
                data = snapshot.file.readBytes()
            )
        ).apply {
            Assert.assertEquals(480, width)
            Assert.assertEquals(480, height)
            Assert.assertEquals(500, duration)
            Assert.assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            Assert.assertEquals(240, width)
            Assert.assertEquals(240, height)
            Assert.assertNotNull(createGifDrawable().apply { recycle() })
        }

        GifInfoHandleHelper(
            DiskCacheDataSource(
                sketch = sketch,
                request = LoadRequest(context, newAssetUri("sample_anim.gif")),
                dataFrom = LOCAL,
                snapshot = snapshot
            )
        ).apply {
            Assert.assertEquals(480, width)
            Assert.assertEquals(480, height)
            Assert.assertEquals(500, duration)
            Assert.assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            Assert.assertEquals(240, width)
            Assert.assertEquals(240, height)
            Assert.assertNotNull(createGifDrawable().apply { recycle() })
        }

        GifInfoHandleHelper(
            ResourceDataSource(
                sketch = sketch,
                request = LoadRequest(context, newResourceUri(R.drawable.sample_anim)),
                packageName = context.packageName,
                resources = context.resources,
                drawableId = R.drawable.sample_anim
            )
        ).apply {
            Assert.assertEquals(480, width)
            Assert.assertEquals(480, height)
            Assert.assertEquals(500, duration)
            Assert.assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            Assert.assertEquals(240, width)
            Assert.assertEquals(240, height)
            Assert.assertNotNull(createGifDrawable().apply { recycle() })
        }

        GifInfoHandleHelper(
            ContentDataSource(
                sketch = sketch,
                request = LoadRequest(context, Uri.fromFile(snapshot.file).toString()),
                contentUri = Uri.fromFile(snapshot.file),
            )
        ).apply {
            Assert.assertEquals(480, width)
            Assert.assertEquals(480, height)
            Assert.assertEquals(500, duration)
            Assert.assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            Assert.assertEquals(240, width)
            Assert.assertEquals(240, height)
            Assert.assertNotNull(createGifDrawable().apply { recycle() })
        }

        GifInfoHandleHelper(
            FileDataSource(
                sketch = sketch,
                request = LoadRequest(context, Uri.fromFile(snapshot.file).toString()),
                file = snapshot.file,
            )
        ).apply {
            Assert.assertEquals(480, width)
            Assert.assertEquals(480, height)
            Assert.assertEquals(500, duration)
            Assert.assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            Assert.assertEquals(240, width)
            Assert.assertEquals(240, height)
            Assert.assertNotNull(createGifDrawable().apply { recycle() })
        }

        GifInfoHandleHelper(
            AssetDataSource(
                sketch = sketch,
                request = LoadRequest(context, newAssetUri("sample_anim.gif")),
                assetFileName = "sample_anim.gif"
            )
        ).apply {
            Assert.assertEquals(480, width)
            Assert.assertEquals(480, height)
            Assert.assertEquals(500, duration)
            Assert.assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            Assert.assertEquals(240, width)
            Assert.assertEquals(240, height)
            Assert.assertNotNull(createGifDrawable().apply { recycle() })
        }

        assertThrow(Exception::class) {
            GifInfoHandleHelper(
                object : UnavailableDataSource {
                    override val sketch: Sketch
                        get() = throw UnsupportedOperationException()
                    override val request: ImageRequest
                        get() = throw UnsupportedOperationException()
                    override val dataFrom: DataFrom
                        get() = throw UnsupportedOperationException()

                    override fun length(): Long {
                        throw UnsupportedOperationException()
                    }

                    override fun newInputStream(): InputStream {
                        throw UnsupportedOperationException()
                    }
                }
            ).apply {
                Assert.assertEquals(480, width)
            }
        }
    }
}