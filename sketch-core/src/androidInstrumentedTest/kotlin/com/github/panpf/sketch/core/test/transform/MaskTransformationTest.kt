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
package com.github.panpf.sketch.core.test.transform

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transform.createMaskTransformed
import com.github.panpf.sketch.transform.getMaskTransformed
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaskTransformationTest {

    @Test
    fun testConstructor() {
        MaskTransformation(Color.BLACK).apply {
            Assert.assertEquals(Color.BLACK, maskColor)
        }
        MaskTransformation(Color.GREEN).apply {
            Assert.assertEquals(Color.GREEN, maskColor)
        }
    }

    @Test
    fun testKeyAndToString() {
        MaskTransformation(Color.BLACK).apply {
            Assert.assertEquals("MaskTransformation(${Color.BLACK})", key)
            Assert.assertEquals("MaskTransformation(${Color.BLACK})", toString())
        }
        MaskTransformation(Color.GREEN).apply {
            Assert.assertEquals("MaskTransformation(${Color.GREEN})", key)
            Assert.assertEquals("MaskTransformation(${Color.GREEN})", toString())
        }
    }

    @Test
    fun testTransform() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, AssetImages.jpeg.uri)
        val inBitmap = context.assets.open(AssetImages.jpeg.fileName).use {
            BitmapFactory.decodeStream(it)
        }.apply {
            Assert.assertNotEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                this.corners()
            )
            Assert.assertEquals(
                Size(1291, 1936),
                this.size
            )
            Assert.assertFalse(this.isMutable)
        }
        val inBitmapCorners = inBitmap.corners()

        val maskColor = ColorUtils.setAlphaComponent(Color.GREEN, 100)
        runBlocking {
            MaskTransformation(maskColor).transform(sketch, request.toRequestContext(sketch), inBitmap)
        }.apply {
            Assert.assertNotSame(inBitmap, this)
            Assert.assertNotEquals(inBitmapCorners, bitmap.corners())
            Assert.assertEquals(Size(1291, 1936), bitmap.size)
            Assert.assertEquals(createMaskTransformed(maskColor), transformed)
        }

        val mutableInBitmap = context.assets.open(AssetImages.jpeg.fileName).use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply {
                inMutable = true
            })
        }!!.apply {
            Assert.assertTrue(this.isMutable)
        }

        runBlocking {
            MaskTransformation(maskColor).transform(
                sketch,
                request.toRequestContext(sketch),
                mutableInBitmap
            )
        }.apply {
            Assert.assertSame(mutableInBitmap, this.bitmap)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = MaskTransformation(Color.RED)
        val element11 = MaskTransformation(Color.RED)
        val element2 = MaskTransformation(Color.BLACK)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testMaskTransformed() {
        Assert.assertEquals("MaskTransformed(1)", createMaskTransformed(1))
        Assert.assertEquals("MaskTransformed(2)", createMaskTransformed(2))
        Assert.assertEquals("MaskTransformed(4)", createMaskTransformed(4))
        Assert.assertEquals("MaskTransformed(8)", createMaskTransformed(8))

        Assert.assertEquals(null, listOf<String>().getMaskTransformed())
        Assert.assertEquals(
            "MaskTransformed(2)",
            listOf(createMaskTransformed(2)).getMaskTransformed()
        )
        Assert.assertEquals(
            "MaskTransformed(16)",
            listOf(
                "disruptive1",
                createMaskTransformed(16),
                "disruptive2"
            ).getMaskTransformed()
        )
    }
}