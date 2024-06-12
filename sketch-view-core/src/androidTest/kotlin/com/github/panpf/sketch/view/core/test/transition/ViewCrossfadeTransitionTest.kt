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
package com.github.panpf.sketch.view.core.test.transition

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.target.ImageViewTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transition.ViewCrossfadeTransition
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import com.github.panpf.tools4j.reflect.ktx.setFieldValue
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewCrossfadeTransitionTest {

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageView = ImageView(context)
        val imageViewTarget = ImageViewTarget(imageView)
        val request = ImageRequest(context, MyImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val resultDrawable =
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 200, RGB_565))
        val result = ImageResult.Success(
            request = request,
            cacheKey = requestContext.cacheKey,
            image = resultDrawable.asSketchImage(),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            dataFrom = LOCAL,
            transformeds = null,
            extras = null,
        )
        ViewCrossfadeTransition(requestContext, imageViewTarget, result).apply {
            Assert.assertEquals(200, durationMillis)
            Assert.assertEquals(false, preferExactIntrinsicSize)
            Assert.assertEquals(true, fitScale)
        }
        ViewCrossfadeTransition(
            requestContext = requestContext,
            target = imageViewTarget,
            result = result,
            durationMillis = 300,
            preferExactIntrinsicSize = true,
            fitScale = false
        ).apply {
            Assert.assertEquals(300, durationMillis)
            Assert.assertEquals(true, preferExactIntrinsicSize)
            Assert.assertEquals(false, fitScale)
        }
        assertThrow(IllegalArgumentException::class) {
            ViewCrossfadeTransition(requestContext, imageViewTarget, result, durationMillis = 0)
        }
    }

    @Test
    fun testTransition() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, MyImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)

        val imageView = ImageView(context)
        val imageViewTarget = ImageViewTarget(imageView)

        Assert.assertNull(imageView.drawable)
        Assert.assertNull(imageViewTarget.drawable)
        Assert.assertEquals(false, imageViewTarget.getFieldValue<Boolean>("isStarted"))
        imageViewTarget.setFieldValue("isStarted", true)
        Assert.assertEquals(true, imageViewTarget.getFieldValue<Boolean>("isStarted"))

        // success
        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = ColorDrawable(Color.GREEN)
        }
        Assert.assertEquals(Color.GREEN, (imageView.drawable as ColorDrawable).color)
        Assert.assertEquals(Color.GREEN, (imageViewTarget.drawable as ColorDrawable).color)
        val resultDrawable =
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 200, RGB_565))
        val success = ImageResult.Success(
            request = request,
            cacheKey = request.toRequestContext(sketch).cacheKey,
            image = resultDrawable.asSketchImage(),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            dataFrom = LOCAL,
            transformeds = null,
            extras = null,
        )
        ViewCrossfadeTransition(requestContext, imageViewTarget, success).transition()
        (imageView.drawable as CrossfadeDrawable).apply {
            Assert.assertEquals(Color.GREEN, (start as ColorDrawable).color)
            Assert.assertTrue(end is BitmapDrawable)
            Assert.assertTrue(fitScale)
        }

        // error
        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = ColorDrawable(Color.GREEN)
        }
        Assert.assertEquals(Color.GREEN, (imageView.drawable as ColorDrawable).color)
        Assert.assertEquals(Color.GREEN, (imageViewTarget.drawable as ColorDrawable).color)
        val error = ImageResult.Error(
            request = request,
            image = resultDrawable.asSketchImage(),
            throwable = Exception(""),
        )
        ViewCrossfadeTransition(requestContext, imageViewTarget, error).transition()
        (imageView.drawable as CrossfadeDrawable).apply {
            Assert.assertEquals(Color.GREEN, (start as ColorDrawable).color)
            Assert.assertTrue(end is BitmapDrawable)
        }

        // start end same
        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = ColorDrawable(Color.GREEN)
        }
        Assert.assertTrue(imageViewTarget.drawable!! is ColorDrawable)
        ViewCrossfadeTransition(
            requestContext = requestContext,
            target = imageViewTarget,
            result = ImageResult.Success(
                request = request,
                cacheKey = request.toRequestContext(sketch).cacheKey,
                image = imageViewTarget.drawable!!.asSketchImage(),
                imageInfo = ImageInfo(100, 200, "image/jpeg"),
                dataFrom = LOCAL,
                transformeds = null,
                extras = null,
            )
        ).transition()
        Assert.assertTrue(imageViewTarget.drawable!! is ColorDrawable)
    }

    @Test
    fun testFactoryConstructor() {
        ViewCrossfadeTransition.Factory().apply {
            Assert.assertEquals(200, durationMillis)
            Assert.assertEquals(false, preferExactIntrinsicSize)
            Assert.assertEquals(false, alwaysUse)
        }

        assertThrow(IllegalArgumentException::class) {
            ViewCrossfadeTransition.Factory(0)
        }

        ViewCrossfadeTransition.Factory(
            durationMillis = 300,
            preferExactIntrinsicSize = true,
            alwaysUse = true
        ).apply {
            Assert.assertEquals(300, durationMillis)
            Assert.assertEquals(true, preferExactIntrinsicSize)
            Assert.assertEquals(true, alwaysUse)
        }
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, MyImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val factory = ViewCrossfadeTransition.Factory()

        val imageView = ImageView(context)
        val imageViewTarget = ImageViewTarget(imageView)

        val resultDrawable =
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 200, RGB_565))

        val successResult = ImageResult.Success(
            request = request,
            cacheKey = request.toRequestContext(sketch).cacheKey,
            image = resultDrawable.asSketchImage(),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            dataFrom = LOCAL,
            transformeds = null,
            extras = null,
        )
        Assert.assertNotNull(factory.create(requestContext, imageViewTarget, successResult))

        val errorResult = ImageResult.Error(
            request = request,
            image = resultDrawable.asSketchImage(),
            throwable = Exception("")
        )
        Assert.assertNotNull(factory.create(requestContext, imageViewTarget, errorResult))

        val fromMemoryCacheSuccessResult = ImageResult.Success(
            request = request,
            cacheKey = request.toRequestContext(sketch).cacheKey,
            image = resultDrawable.asSketchImage(),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            dataFrom = MEMORY_CACHE,
            transformeds = null,
            extras = null,
        )
        Assert.assertNull(
            factory.create(
                requestContext,
                imageViewTarget,
                fromMemoryCacheSuccessResult
            )
        )

        val alwaysUseFactory = ViewCrossfadeTransition.Factory(alwaysUse = true)
        Assert.assertNotNull(
            alwaysUseFactory.create(requestContext, imageViewTarget, fromMemoryCacheSuccessResult)
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = ViewCrossfadeTransition.Factory()
        val element11 = ViewCrossfadeTransition.Factory()
        val element2 = ViewCrossfadeTransition.Factory(durationMillis = 300)
        val element3 = ViewCrossfadeTransition.Factory(preferExactIntrinsicSize = true)
        val element4 = ViewCrossfadeTransition.Factory(alwaysUse = true)
        val element5 = ViewCrossfadeTransition.Factory(fadeStart = false)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element1, element4)
        Assert.assertNotSame(element1, element5)
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
        Assert.assertNotEquals(element1, element5)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element2, element4)
        Assert.assertNotEquals(element3, element4)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element5.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testFactoryToString() {
        val element1 = ViewCrossfadeTransition.Factory()
        val element2 = ViewCrossfadeTransition.Factory(preferExactIntrinsicSize = true)
        val element3 = ViewCrossfadeTransition.Factory(alwaysUse = true)
        val element4 = ViewCrossfadeTransition.Factory(fadeStart = false)

        Assert.assertEquals(
            "ViewCrossfadeTransition.Factory(durationMillis=200, fadeStart=true, preferExactIntrinsicSize=false, alwaysUse=false)",
            element1.toString()
        )
        Assert.assertEquals(
            "ViewCrossfadeTransition.Factory(durationMillis=200, fadeStart=true, preferExactIntrinsicSize=true, alwaysUse=false)",
            element2.toString()
        )
        Assert.assertEquals(
            "ViewCrossfadeTransition.Factory(durationMillis=200, fadeStart=true, preferExactIntrinsicSize=false, alwaysUse=true)",
            element3.toString()
        )
        Assert.assertEquals(
            "ViewCrossfadeTransition.Factory(durationMillis=200, fadeStart=false, preferExactIntrinsicSize=false, alwaysUse=false)",
            element4.toString()
        )
    }
}