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
import android.widget.ImageView.ScaleType
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asImage
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
import com.github.panpf.sketch.test.utils.fakeSuccessImageResult
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transition.ViewCrossfadeTransition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
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
        val resultBitmap = Bitmap.createBitmap(100, 200, RGB_565)
        val result = ImageResult.Success(
            request = request,
            cacheKey = requestContext.cacheKey,
            memoryCacheKey = requestContext.memoryCacheKey,
            resultCacheKey = requestContext.resultCacheKey,
            downloadCacheKey = requestContext.downloadCacheKey,
            image = resultBitmap.asImage(),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            dataFrom = LOCAL,
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            transformeds = null,
            extras = null,
        )

        ViewCrossfadeTransition(
            sketch = sketch,
            request = request,
            target = imageViewTarget,
            result = result
        ).apply {
            assertEquals(200, durationMillis)
            assertEquals(false, preferExactIntrinsicSize)
            assertEquals(ScaleType.FIT_CENTER, scaleType)
            assertEquals(true, fitScale)
        }
        ViewCrossfadeTransition(
            sketch = sketch,
            request = request,
            target = imageViewTarget,
            result = result,
            durationMillis = 300,
            preferExactIntrinsicSize = true,
            scaleType = ScaleType.FIT_XY
        ).apply {
            assertEquals(300, durationMillis)
            assertEquals(true, preferExactIntrinsicSize)
            assertEquals(ScaleType.FIT_XY, scaleType)
            assertEquals(false, fitScale)
        }

        ViewCrossfadeTransition(
            sketch = sketch,
            request = request,
            target = imageViewTarget,
            result = result,
            durationMillis = 300,
            preferExactIntrinsicSize = true,
            fitScale = true
        ).apply {
            assertEquals(300, durationMillis)
            assertEquals(true, preferExactIntrinsicSize)
            assertEquals(ScaleType.FIT_CENTER, scaleType)
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
            assertEquals(ScaleType.CENTER_CROP, scaleType)
            assertEquals(false, fitScale)
        }

        assertFailsWith(IllegalArgumentException::class) {
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
        assertEquals(false, imageViewTarget.isStarted)
        imageViewTarget.isStarted = true
        assertEquals(true, imageViewTarget.isStarted)

        // success
        withContext(Dispatchers.Main) {
            imageViewTarget.onSuccess(
                sketch,
                request,
                fakeSuccessImageResult(context),
                ColorDrawable(Color.GREEN).asImage()
            )
        }
        assertEquals(Color.GREEN, (imageView.drawable as ColorDrawable).color)
        assertEquals(Color.GREEN, (imageViewTarget.drawable as ColorDrawable).color)
        val resultBitmap = Bitmap.createBitmap(100, 200, RGB_565)
        val requestContext = request.toRequestContext(sketch)
        val success = ImageResult.Success(
            request = request,
            cacheKey = requestContext.cacheKey,
            memoryCacheKey = requestContext.memoryCacheKey,
            resultCacheKey = requestContext.resultCacheKey,
            downloadCacheKey = requestContext.downloadCacheKey,
            image = resultBitmap.asImage(),
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
            assertEquals(ScaleType.FIT_CENTER, scaleType)
        }

        // error
        withContext(Dispatchers.Main) {
            imageViewTarget.onSuccess(
                sketch,
                request,
                fakeSuccessImageResult(context),
                ColorDrawable(Color.GREEN).asImage()
            )
        }
        assertEquals(Color.GREEN, (imageView.drawable as ColorDrawable).color)
        assertEquals(Color.GREEN, (imageViewTarget.drawable as ColorDrawable).color)
        val error = ImageResult.Error(
            request = request,
            image = resultBitmap.asImage(),
            throwable = Exception(""),
        )
        ViewCrossfadeTransition(sketch, request, imageViewTarget, error).transition()
        (imageView.drawable as CrossfadeDrawable).apply {
            assertEquals(Color.GREEN, (start as ColorDrawable).color)
        }

        // start end same
        withContext(Dispatchers.Main) {
            imageViewTarget.onSuccess(
                sketch,
                request,
                fakeSuccessImageResult(context),
                ColorDrawable(Color.GREEN).asImage()
            )
        }
        assertTrue(imageViewTarget.drawable!! is ColorDrawable)
        ViewCrossfadeTransition(
            sketch = sketch,
            request = request,
            target = imageViewTarget,
            result = ImageResult.Success(
                request = request,
                cacheKey = requestContext.cacheKey,
                memoryCacheKey = requestContext.memoryCacheKey,
                resultCacheKey = requestContext.resultCacheKey,
                downloadCacheKey = requestContext.downloadCacheKey,
                image = imageViewTarget.drawable!!.asImage(),
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

        assertFailsWith(IllegalArgumentException::class) {
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
        val factory = ViewCrossfadeTransition.Factory()

        val imageView = ImageView(context)
        val imageViewTarget = ImageViewTarget(imageView)

        val resultBitmap = Bitmap.createBitmap(100, 200, RGB_565)

        val requestContext = request.toRequestContext(sketch)
        val successResult = ImageResult.Success(
            request = request,
            cacheKey = requestContext.cacheKey,
            memoryCacheKey = requestContext.memoryCacheKey,
            resultCacheKey = requestContext.resultCacheKey,
            downloadCacheKey = requestContext.downloadCacheKey,
            image = resultBitmap.asImage(),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            dataFrom = LOCAL,
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            transformeds = null,
            extras = null,
        )
        assertNotNull(factory.create(sketch, request, imageViewTarget, successResult))

        val errorResult = ImageResult.Error(
            request = request,
            image = resultBitmap.asImage(),
            throwable = Exception("")
        )
        assertNotNull(factory.create(sketch, request, imageViewTarget, errorResult))

        val fromMemoryCacheSuccessResult = ImageResult.Success(
            request = request,
            cacheKey = requestContext.cacheKey,
            memoryCacheKey = requestContext.memoryCacheKey,
            resultCacheKey = requestContext.resultCacheKey,
            downloadCacheKey = requestContext.downloadCacheKey,
            image = resultBitmap.asImage(),
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

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testFactoryKey() {
        val element1 = ViewCrossfadeTransition.Factory()
        val element2 = ViewCrossfadeTransition.Factory(preferExactIntrinsicSize = true)
        val element3 = ViewCrossfadeTransition.Factory(alwaysUse = true)
        val element4 = ViewCrossfadeTransition.Factory(fadeStart = false)

        assertEquals(
            "ViewCrossfade(200,true,false,false)",
            element1.key
        )
        assertEquals(
            "ViewCrossfade(200,true,true,false)",
            element2.key
        )
        assertEquals(
            "ViewCrossfade(200,true,false,true)",
            element3.key
        )
        assertEquals(
            "ViewCrossfade(200,false,false,false)",
            element4.key
        )
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