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
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.BitmapDecodeException
import com.github.panpf.sketch.decode.internal.XmlDrawableBitmapDecoder
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.test.R
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.util.toShortInfoString
import com.github.panpf.tools4a.dimen.ktx.dp2px
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class XmlDrawableBitmapDecoderTest {

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndNewSketch()
        val factory = XmlDrawableBitmapDecoder.Factory()

        Assert.assertEquals("XmlDrawableBitmapDecoder", factory.toString())

        LoadRequest(context, newResourceUri(R.drawable.test)).let {
            val fetcher = sketch.components.newFetcher(it)
            val fetchResult = runBlocking { fetcher.fetch() }
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        LoadRequest(context, newResourceUri(R.drawable.test_error)).let {
            val fetcher = sketch.components.newFetcher(it)
            val fetchResult = runBlocking { fetcher.fetch() }
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        LoadRequest(context, newResourceUri(R.drawable.ic_launcher)).let {
            val fetcher = sketch.components.newFetcher(it)
            val fetchResult = runBlocking { fetcher.fetch() }
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testDecode() {
        val (context, sketch) = getTestContextAndNewSketch()
        val factory = XmlDrawableBitmapDecoder.Factory()

        LoadRequest(context, newResourceUri(R.drawable.test)).run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking {
                fetcher.fetch()
            }
            runBlocking {
                factory.create(sketch, this@run, RequestContext(this@run), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals(
                "Bitmap(${50.dp2px}x${40.dp2px},ARGB_8888)",
                bitmap.toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${50.dp2px}x${40.dp2px},'image/android-xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, newResourceUri(R.drawable.test_error)).run {
            val fetcher = sketch.components.newFetcher(this)
            val fetchResult = runBlocking {
                fetcher.fetch()
            }
            assertThrow(BitmapDecodeException::class) {
                runBlocking {
                    factory.create(sketch, this@run, RequestContext(this@run), fetchResult)!!
                        .decode()
                }
            }
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = XmlDrawableBitmapDecoder.Factory()
        val element11 = XmlDrawableBitmapDecoder.Factory()
        val element2 = XmlDrawableBitmapDecoder.Factory()

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