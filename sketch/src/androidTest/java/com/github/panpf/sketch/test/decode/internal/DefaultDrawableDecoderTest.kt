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
package com.github.panpf.sketch.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.ratio
import com.github.panpf.sketch.test.utils.samplingByTarget
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultDrawableDecoderTest {

    @Test
    fun testDecode() {
        val (context, sketch) = getTestContextAndNewSketch()
        val imageSize = Size(1291, 1936)
        val resizeSize = Size(500, 400)
        val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            resultCachePolicy(DISABLED)
            resizeSize(resizeSize)
            resizePrecision(LESS_PIXELS)
        }

        request.let {
            runBlocking {
                val fetchResult = sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                DefaultDrawableDecoder.Factory()
                    .create(sketch, it.toRequestContext(), fetchResult)
                    .decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals(samplingByTarget(imageSize, resizeSize), drawable.intrinsicSize)
            Assert.assertEquals(imageInfo.size.ratio, drawable.intrinsicSize.ratio)
            Assert.assertEquals(ImageInfo(1291, 1936, "image/jpeg", 1), imageInfo)
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertEquals(listOf("InSampledTransformed(4)"), transformedList)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = DefaultDrawableDecoder.Factory()
        val element11 = DefaultDrawableDecoder.Factory()
        val element2 = DefaultDrawableDecoder.Factory()

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertEquals(element1, element2)
        Assert.assertEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertEquals(element1.hashCode(), element2.hashCode())
        Assert.assertEquals(element2.hashCode(), element11.hashCode())
    }
}