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
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataFrom.NETWORK
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.DiskCacheDataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.tools4j.test.ktx.assertThrow
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
            request = ImageRequest(context, MyImages.animGif.uri),
            assetFileName = MyImages.animGif.fileName
        ).getFile()
        val snapshot = sketch.resultCache[MyImages.animGif.uri + "_data_source"]!!

        GifInfoHandleHelper(
            ByteArrayDataSource(
                sketch = sketch,
                request = ImageRequest(context, "http://sample.com/sample.gif"),
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
                request = ImageRequest(context, MyImages.animGif.uri),
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
                request = ImageRequest(context, Uri.fromFile(snapshot.file).toString()),
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
                request = ImageRequest(context, Uri.fromFile(snapshot.file).toString()),
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
                request = ImageRequest(context, MyImages.animGif.uri),
                assetFileName = MyImages.animGif.fileName
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
                        get() = ImageRequest(context, Uri.fromFile(snapshot.file).toString())
                    override val dataFrom: DataFrom
                        get() = LOCAL
                }
            ).width
        }
    }
}