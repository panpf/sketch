/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
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
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ViewCrossfadeTransitionTest {

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageView = ImageView(context)
        val imageViewTarget = ImageViewTarget(imageView)
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val resultDrawable =
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 200, RGB_565))
        val result = ImageResult.Success(
            request = request,
            cacheKey = requestContext.cacheKey,
            image = resultDrawable.asSketchImage(),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            dataFrom = LOCAL,
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            transformeds = null,
            extras = null,
        )
        ViewCrossfadeTransition(sketch, request, imageViewTarget, result).apply {
            assertEquals(200, durationMillis)
            assertEquals(false, preferExactIntrinsicSize)
            assertEquals(true, fitScale)
        }
        ViewCrossfadeTransition(
            sketch = sketch,
            request = request,
            target = imageViewTarget,
            result = result,
            durationMillis = 300,
            preferExactIntrinsicSize = true,
            fitScale = false
        ).apply {
            assertEquals(300, durationMillis)
            assertEquals(true, preferExactIntrinsicSize)
            assertEquals(false, fitScale)
        }
        assertThrow(IllegalArgumentException::class) {
            ViewCrossfadeTransition(sketch, request, imageViewTarget, result, durationMillis = 0)
        }
    }

    @Test
    fun testTransition() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        val imageView = ImageView(context)
        val imageViewTarget = ImageViewTarget(imageView)

        assertNull(imageView.drawable)
        assertNull(imageViewTarget.drawable)
        assertEquals(false, imageViewTarget.getFieldValue<Boolean>("isStarted"))
        imageViewTarget.setFieldValue("isStarted", true)
        assertEquals(true, imageViewTarget.getFieldValue<Boolean>("isStarted"))

        // success
        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = ColorDrawable(Color.GREEN)
        }
        assertEquals(Color.GREEN, (imageView.drawable as ColorDrawable).color)
        assertEquals(Color.GREEN, (imageViewTarget.drawable as ColorDrawable).color)
        val resultDrawable =
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 200, RGB_565))
        val success = ImageResult.Success(
            request = request,
            cacheKey = request.toRequestContext(sketch).cacheKey,
            image = resultDrawable.asSketchImage(),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            dataFrom = LOCAL,
            transformeds = null,
            extras = null,
        )
        ViewCrossfadeTransition(sketch, request, imageViewTarget, success).transition()
        (imageView.drawable as CrossfadeDrawable).apply {
            assertEquals(Color.GREEN, (start as ColorDrawable).color)
            assertTrue(end is BitmapDrawable)
            assertTrue(fitScale)
        }

        // error
        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = ColorDrawable(Color.GREEN)
        }
        assertEquals(Color.GREEN, (imageView.drawable as ColorDrawable).color)
        assertEquals(Color.GREEN, (imageViewTarget.drawable as ColorDrawable).color)
        val error = ImageResult.Error(
            request = request,
            image = resultDrawable.asSketchImage(),
            throwable = Exception(""),
        )
        ViewCrossfadeTransition(sketch, request, imageViewTarget, error).transition()
        (imageView.drawable as CrossfadeDrawable).apply {
            assertEquals(Color.GREEN, (start as ColorDrawable).color)
            assertTrue(end is BitmapDrawable)
        }

        // start end same
        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = ColorDrawable(Color.GREEN)
        }
        assertTrue(imageViewTarget.drawable!! is ColorDrawable)
        ViewCrossfadeTransition(
            sketch = sketch,
            request = request,
            target = imageViewTarget,
            result = ImageResult.Success(
                request = request,
                cacheKey = request.toRequestContext(sketch).cacheKey,
                image = imageViewTarget.drawable!!.asSketchImage(),
                imageInfo = ImageInfo(100, 200, "image/jpeg"),
                dataFrom = LOCAL,
                resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
                transformeds = null,
                extras = null,
            )
        ).transition()
        assertTrue(imageViewTarget.drawable!! is ColorDrawable)
    }

    @Test
    fun testFactoryConstructor() {
        ViewCrossfadeTransition.Factory().apply {
            assertEquals(200, durationMillis)
            assertEquals(false, preferExactIntrinsicSize)
            assertEquals(false, alwaysUse)
        }

        assertThrow(IllegalArgumentException::class) {
            ViewCrossfadeTransition.Factory(0)
        }

        ViewCrossfadeTransition.Factory(
            durationMillis = 300,
            preferExactIntrinsicSize = true,
            alwaysUse = true
        ).apply {
            assertEquals(300, durationMillis)
            assertEquals(true, preferExactIntrinsicSize)
            assertEquals(true, alwaysUse)
        }
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
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
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            transformeds = null,
            extras = null,
        )
        assertNotNull(factory.create(sketch, request, imageViewTarget, successResult))

        val errorResult = ImageResult.Error(
            request = request,
            image = resultDrawable.asSketchImage(),
            throwable = Exception("")
        )
        assertNotNull(factory.create(sketch, request, imageViewTarget, errorResult))

        val fromMemoryCacheSuccessResult = ImageResult.Success(
            request = request,
            cacheKey = request.toRequestContext(sketch).cacheKey,
            image = resultDrawable.asSketchImage(),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            dataFrom = MEMORY_CACHE,
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            transformeds = null,
            extras = null,
        )
        assertNull(
            factory.create(
                sketch = sketch,
                request = request,
                target = imageViewTarget,
                result = fromMemoryCacheSuccessResult
            )
        )

        val alwaysUseFactory = ViewCrossfadeTransition.Factory(alwaysUse = true)
        assertNotNull(
            alwaysUseFactory.create(sketch, request, imageViewTarget, fromMemoryCacheSuccessResult)
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

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element1, element4)
        assertNotSame(element1, element5)
        assertNotSame(element11, element2)
        assertNotSame(element11, element3)
        assertNotSame(element11, element4)
        assertNotSame(element2, element3)
        assertNotSame(element2, element4)
        assertNotSame(element3, element4)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testFactoryToString() {
        val element1 = ViewCrossfadeTransition.Factory()
        val element2 = ViewCrossfadeTransition.Factory(preferExactIntrinsicSize = true)
        val element3 = ViewCrossfadeTransition.Factory(alwaysUse = true)
        val element4 = ViewCrossfadeTransition.Factory(fadeStart = false)

        assertEquals(
            "ViewCrossfadeTransition.Factory(durationMillis=200, fadeStart=true, preferExactIntrinsicSize=false, alwaysUse=false)",
            element1.toString()
        )
        assertEquals(
            "ViewCrossfadeTransition.Factory(durationMillis=200, fadeStart=true, preferExactIntrinsicSize=true, alwaysUse=false)",
            element2.toString()
        )
        assertEquals(
            "ViewCrossfadeTransition.Factory(durationMillis=200, fadeStart=true, preferExactIntrinsicSize=false, alwaysUse=true)",
            element3.toString()
        )
        assertEquals(
            "ViewCrossfadeTransition.Factory(durationMillis=200, fadeStart=false, preferExactIntrinsicSize=false, alwaysUse=false)",
            element4.toString()
        )
    }
}