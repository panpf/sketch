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
package com.github.panpf.sketch.core.android.test.transform

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.createBlurTransformed
import com.github.panpf.sketch.transform.getBlurTransformed
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BlurTransformationTest {

    @Test
    fun testConstructor() {
        assertThrow(IllegalArgumentException::class) {
            BlurTransformation(0)
        }
        assertThrow(IllegalArgumentException::class) {
            BlurTransformation(101)
        }
        assertThrow(IllegalArgumentException::class) {
            BlurTransformation(
                hasAlphaBitmapBgColor = ColorUtils.setAlphaComponent(Color.BLACK, 244)
            )
        }
        BlurTransformation(12).apply {
            Assert.assertEquals(12, radius)
            Assert.assertEquals(Color.BLACK, hasAlphaBitmapBgColor)
            Assert.assertNull(maskColor)
        }
        BlurTransformation(20, hasAlphaBitmapBgColor = null, maskColor = Color.GREEN).apply {
            Assert.assertEquals(20, radius)
            Assert.assertNull(hasAlphaBitmapBgColor)
            Assert.assertEquals(Color.GREEN, maskColor)
        }
    }

    @Test
    fun testKeyAndToString() {
        BlurTransformation().apply {
            Assert.assertEquals("BlurTransformation(15,${Color.BLACK},null)", key)
            Assert.assertEquals("BlurTransformation(15,${Color.BLACK},null)", toString())
        }
        BlurTransformation(20, hasAlphaBitmapBgColor = null, maskColor = Color.GREEN).apply {
            Assert.assertEquals("BlurTransformation(20,null,${Color.GREEN})", key)
            Assert.assertEquals("BlurTransformation(20,null,${Color.GREEN})", toString())
        }
    }

    @Test
    fun testTransform() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        // isMutable false
        val inBitmap = context.assets.open(ResourceImages.jpeg.resourceName).use {
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
        runBlocking {
            BlurTransformation(
                30,
                maskColor = ColorUtils.setAlphaComponent(Color.BLUE, 80)
            ).transform(sketch, request.toRequestContext(sketch), inBitmap.asSketchImage())
        }.apply {
            Assert.assertNotSame(inBitmap, image.getBitmapOrThrow())
            Assert.assertNotEquals(inBitmapCorners, image.getBitmapOrThrow().corners())
            Assert.assertEquals(Size(1291, 1936), image.getBitmapOrThrow().size)
            Assert.assertEquals(
                createBlurTransformed(
                    30,
                    Color.BLACK,
                    ColorUtils.setAlphaComponent(Color.BLUE, 80)
                ), transformed
            )
        }

        // isMutable true
        val mutableInBitmap = context.assets.open(ResourceImages.jpeg.resourceName).use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply {
                inMutable = true
            })
        }!!.apply {
            Assert.assertTrue(this.isMutable)
        }
        runBlocking {
            BlurTransformation(30).transform(
                sketch,
                request.toRequestContext(sketch),
                mutableInBitmap.asSketchImage()
            )
        }.apply {
            Assert.assertSame(mutableInBitmap, this.image.getBitmapOrThrow())
        }

        // hasAlphaBitmapBgColor
        val hasAlphaBitmap1 = context.assets.open(ResourceImages.png.resourceName).use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            Assert.assertTrue(this.hasAlpha())
        }
        val hasAlphaBitmapBlurred1 = runBlocking {
            BlurTransformation(30).transform(
                sketch,
                request.toRequestContext(sketch),
                hasAlphaBitmap1.asSketchImage()
            )
        }.apply {
            Assert.assertTrue(this.image.getBitmapOrThrow().hasAlpha())
        }.image.getBitmapOrThrow()

        val hasAlphaBitmap2 = context.assets.open(ResourceImages.png.resourceName).use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            Assert.assertTrue(this.hasAlpha())
        }
        val hasAlphaBitmapBlurred2 = runBlocking {
            BlurTransformation(30, hasAlphaBitmapBgColor = null)
                .transform(
                    sketch,
                    request.toRequestContext(sketch),
                    hasAlphaBitmap2.asSketchImage()
                )
        }.apply {
            Assert.assertTrue(this.image.getBitmapOrThrow().hasAlpha())
        }.image.getBitmapOrThrow()
        Assert.assertNotEquals(hasAlphaBitmapBlurred1.corners(), hasAlphaBitmapBlurred2.corners())
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurTransformation(20, null, null)
        val element11 = BlurTransformation(20, null, null)
        val element2 = BlurTransformation(10, Color.GREEN, null)
        val element3 = BlurTransformation(20, Color.BLACK, Color.BLUE)
        val element4 = BlurTransformation(20, Color.BLACK, Color.WHITE)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element1, element4)
        Assert.assertNotSame(element11, element2)
        Assert.assertNotSame(element11, element3)
        Assert.assertNotSame(element11, element4)
        Assert.assertNotSame(element2, element3)
        Assert.assertNotSame(element2, element4)
        Assert.assertNotSame(element3, element4)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element1, element4)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element2, element4)
        Assert.assertNotEquals(element3, element4)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testBlurTransformed() {
        Assert.assertEquals("BlurTransformed(1,null,null)", createBlurTransformed(1, null, null))
        Assert.assertEquals("BlurTransformed(2,null,null)", createBlurTransformed(2, null, null))
        Assert.assertEquals("BlurTransformed(4,null,null)", createBlurTransformed(4, null, null))
        Assert.assertEquals("BlurTransformed(8,null,null)", createBlurTransformed(8, null, null))

        Assert.assertEquals(null, listOf<String>().getBlurTransformed())
        Assert.assertEquals(
            "BlurTransformed(2,null,null)",
            listOf(createBlurTransformed(2, null, null)).getBlurTransformed()
        )
        Assert.assertEquals(
            "BlurTransformed(16,null,null)",
            listOf(
                "disruptive1",
                createBlurTransformed(16, null, null),
                "disruptive2"
            ).getBlurTransformed()
        )
    }
}