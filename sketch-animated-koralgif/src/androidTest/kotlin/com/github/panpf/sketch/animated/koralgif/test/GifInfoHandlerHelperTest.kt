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
package com.github.panpf.sketch.animated.koralgif.test

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.ResourceImageFile
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataFrom.NETWORK
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import okio.Path
import okio.Source
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import pl.droidsonroids.gif.GifInfoHandleHelper
import pl.droidsonroids.gif.GifOptions

@RunWith(AndroidJUnit4::class)
class GifInfoHandlerHelperTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, ResourceImages.animGif.uri),
            assetFileName = ResourceImages.animGif.asOrThrow<ResourceImageFile>().resourceName
        ).getFile()
        val snapshot = sketch.resultCache.openSnapshot(ResourceImages.animGif.uri + "_data_source")!!

        GifInfoHandleHelper(
            ByteArrayDataSource(
                sketch = sketch,
                request = ImageRequest(context, "http://sample.com/sample.gif"),
                dataFrom = NETWORK,
                data = snapshot.data.toFile().readBytes()
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
                request = ImageRequest(context, ResourceImages.animGif.uri),
                path = snapshot.data,
                dataFrom = LOCAL,
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
                request = ImageRequest(
                    context,
                    newResourceUri(com.github.panpf.sketch.images.R.raw.sample_anim)
                ),
                packageName = context.packageName,
                resources = context.resources,
                resId = com.github.panpf.sketch.images.R.raw.sample_anim
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
                request = ImageRequest(context, Uri.fromFile(snapshot.data.toFile()).toString()),
                contentUri = Uri.fromFile(snapshot.data.toFile()),
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
                request = ImageRequest(context, Uri.fromFile(snapshot.data.toFile()).toString()),
                path = snapshot.data,
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
                request = ImageRequest(context, ResourceImages.animGif.uri),
                assetFileName = ResourceImages.animGif.asOrThrow<ResourceImageFile>().resourceName
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
                object : DataSource {
                    override val sketch: Sketch
                        get() = sketch
                    override val request: ImageRequest
                        get() = ImageRequest(
                            context,
                            Uri.fromFile(snapshot.data.toFile()).toString()
                        )
                    override val dataFrom: DataFrom
                        get() = LOCAL

                    override fun openSourceOrNull(): Source? = null

                    override fun getFileOrNull(): Path? = null
                }
            ).width
        }
    }
}