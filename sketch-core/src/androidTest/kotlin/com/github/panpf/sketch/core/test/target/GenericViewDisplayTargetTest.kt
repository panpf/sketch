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
package com.github.panpf.sketch.core.test.target

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.core.test.getTestContext
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.target.GenericViewDisplayTarget
import com.github.panpf.sketch.target.ImageViewDisplayTarget
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenericViewDisplayTargetTest {

    // TODO test allowSetNullDrawable

    @Test
    fun testUpdateDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val request = DisplayRequest(context, AssetImages.jpeg.uri)

        val imageView = ImageView(context)
        Assert.assertNull(imageView.drawable)

        val imageViewTarget = TestViewDisplayTarget(imageView)
        Assert.assertNull(imageViewTarget.drawable)

        val countBitmap = CountBitmap(
            cacheKey = request.toRequestContext().cacheKey,
            originBitmap = Bitmap.createBitmap(100, 100, RGB_565),
            bitmapPool = sketch.bitmapPool,
            disallowReuseBitmap = false,
        )
        val sketchCountBitmapDrawable = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = countBitmap,
            imageUri = request.uriString,
            requestKey = request.toRequestContext().key,
            requestCacheKey = request.toRequestContext().cacheKey,
            imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
            transformedList = null,
            extras = null,
            dataFrom = LOCAL
        )
        val countBitmap2 = CountBitmap(
            cacheKey = request.toRequestContext().cacheKey,
            originBitmap = Bitmap.createBitmap(100, 100, RGB_565),
            bitmapPool = sketch.bitmapPool,
            disallowReuseBitmap = false,
        )
        val sketchCountBitmapDrawable2 = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = countBitmap2,
            imageUri = request.uriString,
            requestKey = request.toRequestContext().key,
            requestCacheKey = request.toRequestContext().cacheKey,
            imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
            transformedList = null,
            extras = null,
            dataFrom = LOCAL
        )

        runBlocking(Dispatchers.Main) {
            Assert.assertEquals(0, countBitmap.getDisplayedCount())
            Assert.assertEquals(0, countBitmap2.getDisplayedCount())
        }

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onSuccess(sketchCountBitmapDrawable)
        }

        Assert.assertSame(sketchCountBitmapDrawable, imageView.drawable)
        Assert.assertSame(sketchCountBitmapDrawable, imageViewTarget.drawable)
        runBlocking(Dispatchers.Main) {
            Assert.assertEquals(1, countBitmap.getDisplayedCount())
            Assert.assertEquals(0, countBitmap2.getDisplayedCount())
        }

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onSuccess(sketchCountBitmapDrawable2)
        }

        Assert.assertSame(sketchCountBitmapDrawable2, imageView.drawable)
        Assert.assertSame(sketchCountBitmapDrawable2, imageViewTarget.drawable)
        runBlocking(Dispatchers.Main) {
            Assert.assertEquals(0, countBitmap.getDisplayedCount())
            Assert.assertEquals(1, countBitmap2.getDisplayedCount())
        }

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onError(null)
        }
        Assert.assertNotNull(imageView.drawable)
        Assert.assertNotNull(imageViewTarget.drawable)
    }

    @Test
    fun testIsStarted() {
        val context = getTestContext()
        val imageView = ImageView(context)

        TestViewDisplayTarget(imageView).apply {
            Assert.assertFalse(getFieldValue<Boolean>("isStarted")!!)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertTrue(getFieldValue<Boolean>("isStarted")!!)

            onStop(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertFalse(getFieldValue<Boolean>("isStarted")!!)
        }
    }

    @Test
    fun testAnimatableDrawable() {
        val context = getTestContext()
        val imageView = ImageView(context)

        TestViewDisplayTarget(imageView).apply {
            val drawable = ColorDrawable(Color.RED)
            onStart(drawable)
            onError(drawable)
            onSuccess(drawable)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)

            onStart(drawable)
            onError(drawable)
            onSuccess(drawable)
        }

        TestViewDisplayTarget(imageView).apply {
            val animatableDrawable = AnimatableDrawable(Color.RED)
            Assert.assertFalse(animatableDrawable.running)

            onSuccess(animatableDrawable)
            Assert.assertFalse(animatableDrawable.running)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertTrue(animatableDrawable.running)

            onStop(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertFalse(animatableDrawable.running)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertTrue(animatableDrawable.running)

            onSuccess(ColorDrawable(Color.RED))
            Assert.assertFalse(animatableDrawable.running)

            onSuccess(animatableDrawable)
            Assert.assertTrue(animatableDrawable.running)
        }
    }

    class TestViewDisplayTarget(override val view: ImageView) :
        GenericViewDisplayTarget<ImageView>(view) {
        override var drawable: Drawable?
            get() = view.drawable
            set(value) {
                view.setImageDrawable(value)
            }
    }

    class AnimatableDrawable(color: Int) : ColorDrawable(color), Animatable {
        var running = false

        override fun start() {
            running = true
        }

        override fun stop() {
            running = false
        }

        override fun isRunning(): Boolean {
            return running
        }
    }
}