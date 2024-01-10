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
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.allowSetNullDrawable
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.target.GenericViewTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenericViewTargetTest {

    // TODO test allowSetNullDrawable

    @Test
    fun testUpdateDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, AssetImages.jpeg.uri) {
            allowSetNullDrawable()
        }
        val requestContext = request.toRequestContext(sketch)

        val imageView = ImageView(context)
        Assert.assertNull(imageView.drawable)

        val imageViewTarget = TestViewTarget(imageView)
        Assert.assertNull(imageViewTarget.drawable)

        val countBitmap = CountBitmap(
            cacheKey = request.toRequestContext(sketch).cacheKey,
            originBitmap = Bitmap.createBitmap(100, 100, RGB_565),
            bitmapPool = sketch.bitmapPool,
            disallowReuseBitmap = false,
        )
        val sketchCountBitmapDrawable = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = countBitmap,
        )
        val countBitmap2 = CountBitmap(
            cacheKey = request.toRequestContext(sketch).cacheKey,
            originBitmap = Bitmap.createBitmap(100, 100, RGB_565),
            bitmapPool = sketch.bitmapPool,
            disallowReuseBitmap = false,
        )
        val sketchCountBitmapDrawable2 = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = countBitmap2,
        )

        runBlocking(Dispatchers.Main) {
            Assert.assertEquals(0, countBitmap.getDisplayedCount())
            Assert.assertEquals(0, countBitmap2.getDisplayedCount())
        }

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onSuccess(requestContext, sketchCountBitmapDrawable.asSketchImage())
        }

        Assert.assertSame(sketchCountBitmapDrawable, imageView.drawable)
        Assert.assertSame(sketchCountBitmapDrawable, imageViewTarget.drawable)
        runBlocking(Dispatchers.Main) {
            Assert.assertEquals(1, countBitmap.getDisplayedCount())
            Assert.assertEquals(0, countBitmap2.getDisplayedCount())
        }

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onSuccess(requestContext, sketchCountBitmapDrawable2.asSketchImage())
        }

        Assert.assertSame(sketchCountBitmapDrawable2, imageView.drawable)
        Assert.assertSame(sketchCountBitmapDrawable2, imageViewTarget.drawable)
        runBlocking(Dispatchers.Main) {
            Assert.assertEquals(0, countBitmap.getDisplayedCount())
            Assert.assertEquals(1, countBitmap2.getDisplayedCount())
        }

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onError(requestContext, null)
        }
        Assert.assertNull(imageView.drawable)
        Assert.assertNull(imageViewTarget.drawable)
    }

    @Test
    fun testIsStarted() {
        val context = getTestContext()
        val imageView = ImageView(context)

        TestViewTarget(imageView).apply {
            Assert.assertFalse(getFieldValue<Boolean>("isStarted")!!)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertTrue(getFieldValue<Boolean>("isStarted")!!)

            onStop(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertFalse(getFieldValue<Boolean>("isStarted")!!)
        }
    }

    @Test
    fun testAnimatableDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val imageView = ImageView(context)
        val requestContext = RequestContext(sketch, ImageRequest(context, null))

        TestViewTarget(imageView).apply {
            val drawable = ColorDrawable(Color.RED)
            onStart(requestContext, drawable.asSketchImage())
            onError(requestContext, drawable.asSketchImage())
            onSuccess(requestContext, drawable.asSketchImage())

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)

            onStart(requestContext, drawable.asSketchImage())
            onError(requestContext, drawable.asSketchImage())
            onSuccess(requestContext, drawable.asSketchImage())
        }

        TestViewTarget(imageView).apply {
            val animatableDrawable = AnimatableDrawable(Color.RED)
            Assert.assertFalse(animatableDrawable.running)

            onSuccess(requestContext, animatableDrawable.asSketchImage())
            Assert.assertFalse(animatableDrawable.running)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertTrue(animatableDrawable.running)

            onStop(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertFalse(animatableDrawable.running)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertTrue(animatableDrawable.running)

            onSuccess(requestContext, ColorDrawable(Color.RED).asSketchImage())
            Assert.assertFalse(animatableDrawable.running)

            onSuccess(requestContext, animatableDrawable.asSketchImage())
            Assert.assertTrue(animatableDrawable.running)
        }
    }

    class TestViewTarget(override val view: ImageView) :
        GenericViewTarget<ImageView>(view) {
        override var drawable: Drawable?
            get() = view.drawable
            set(value) {
                view.setImageDrawable(value)
            }
        override val fitScale: Boolean
            get() = view.scaleType.fitScale
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