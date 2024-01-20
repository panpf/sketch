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
package com.github.panpf.sketch.extensions.view.core.test

import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.view.LayoutInflater
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.Listeners
import com.github.panpf.sketch.request.internal.ProgressListeners
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage.UriEmptyCondition
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.ability.removeProgressIndicator
import com.github.panpf.sketch.ability.setClickIgnoreSaveCellularTrafficEnabled
import com.github.panpf.sketch.ability.showMaskProgressIndicator
import com.github.panpf.tools4j.reflect.ktx.callMethod
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchImageViewTest {

    @Test
    fun testAttrs() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_default, null, false) as SketchImageView).apply {
            Assert.assertNull(displayImageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test, null, false) as SketchImageView).apply {
            Assert.assertEquals(ImageOptions {
                bitmapConfig(RGB_565)
                crossfade(3000, preferExactIntrinsicSize = true)
                depth(LOCAL)
                disallowReuseBitmap()
                downloadCachePolicy(WRITE_ONLY)
                ignoreExifOrientation()
                memoryCachePolicy(DISABLED)
                @Suppress("DEPRECATION")
                preferQualityOverSpeed()
                resizeApplyToDrawable()
                resize(354, 2789, SAME_ASPECT_RATIO, FILL)
                resultCachePolicy(READ_ONLY)
                transformations(RoundedCornersTransformation(200f))
            }, displayImageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_state, null, false) as SketchImageView).apply {
            Assert.assertNotNull(displayImageOptions!!.placeholder)
            Assert.assertNotNull(displayImageOptions!!.uriEmpty)
            Assert.assertNotNull(displayImageOptions!!.error)
            Assert.assertNotNull((displayImageOptions!!.error as ErrorStateImage).stateList.find { it.first is UriEmptyCondition })
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_blur, null, false) as SketchImageView).apply {
            Assert.assertEquals(ImageOptions {
                transformations(
                    BlurTransformation(
                        23,
                        hasAlphaBitmapBgColor = Color.parseColor("#0000FF"),
                        maskColor = Color.parseColor("#00FF00")
                    )
                )
            }, displayImageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_rotate, null, false) as SketchImageView).apply {
            Assert.assertEquals(ImageOptions {
                transformations(RotateTransformation(444))
            }, displayImageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_circle, null, false) as SketchImageView).apply {
            Assert.assertEquals(ImageOptions {
                transformations(CircleCropTransformation(END_CROP))
            }, displayImageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_mask, null, false) as SketchImageView).apply {
            Assert.assertEquals(ImageOptions {
                transformations(MaskTransformation(Color.parseColor("#00FF00")))
            }, displayImageOptions)
        }
    }

    @Test
    fun testListener() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketchImageView = SketchImageView(context)

        sketchImageView.getListener().apply {
            Assert.assertTrue(this is Listeners)
            Assert.assertEquals(
                listOf(sketchImageView.requestState),
                (this as Listeners).listenerList
            )
        }

        val listener1 = object : Listener {}
        val listener2 = object : Listener {}

        sketchImageView.registerListener(listener1)
        sketchImageView.getListener()!!.apply {
            Assert.assertTrue(this is Listeners)
            Assert.assertEquals(
                listOf(sketchImageView.requestState, listener1),
                (this as Listeners).listenerList
            )
        }

        sketchImageView.unregisterListener(listener2)
        sketchImageView.getListener()!!.apply {
            Assert.assertTrue(this is Listeners)
            Assert.assertEquals(
                listOf(sketchImageView.requestState, listener1),
                (this as Listeners).listenerList
            )
        }

        sketchImageView.registerListener(listener2)
        sketchImageView.getListener()!!.apply {
            Assert.assertTrue(this is Listeners)
            Assert.assertEquals(
                listOf(sketchImageView.requestState, listener1, listener2),
                (this as Listeners).listenerList
            )
        }

        sketchImageView.setClickIgnoreSaveCellularTrafficEnabled(context.sketch, true)
        val viewAbilityListener = sketchImageView
            .getFieldValue<Any>("viewAbilityManager")!!
            .callMethod<Any>("getRequestListener")
        sketchImageView.getListener()!!.apply {
            Assert.assertTrue(this is Listeners)
            Assert.assertEquals(
                listOf(
                    sketchImageView.requestState,
                    listener1,
                    listener2,
                    viewAbilityListener
                ),
                (this as Listeners).listenerList
            )
        }

        sketchImageView.unregisterListener(listener1)
        sketchImageView.getListener()!!.apply {
            Assert.assertTrue(this is Listeners)
            Assert.assertEquals(
                listOf(sketchImageView.requestState, listener2, viewAbilityListener),
                (this as Listeners).listenerList
            )
        }

        sketchImageView.unregisterListener(listener2)
        sketchImageView.getListener()!!.apply {
            Assert.assertTrue(this is Listeners)
            Assert.assertEquals(
                listOf(sketchImageView.requestState, viewAbilityListener),
                (this as Listeners).listenerList
            )
        }

        sketchImageView.setClickIgnoreSaveCellularTrafficEnabled(context.sketch, false)
        sketchImageView.getListener().apply {
            Assert.assertTrue(this is Listeners)
            Assert.assertEquals(
                listOf(sketchImageView.requestState),
                (this as Listeners).listenerList
            )
        }
    }

    @Test
    fun testProgressListener() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketchImageView = SketchImageView(context)

        Assert.assertNull(sketchImageView.getProgressListener())

        val listener1 = ProgressListener { _, _ -> }
        val listener2 = ProgressListener { _, _ -> }

        sketchImageView.registerProgressListener(listener1)
        sketchImageView.getProgressListener()!!.apply {
            Assert.assertTrue(this is ProgressListeners)
            Assert.assertEquals(listOf(listener1), (this as ProgressListeners).progressListenerList)
        }

        sketchImageView.unregisterProgressListener(listener2)
        sketchImageView.getProgressListener()!!.apply {
            Assert.assertTrue(this is ProgressListeners)
            Assert.assertEquals(listOf(listener1), (this as ProgressListeners).progressListenerList)
        }

        sketchImageView.registerProgressListener(listener2)
        sketchImageView.getProgressListener()!!.apply {
            Assert.assertTrue(this is ProgressListeners)
            Assert.assertEquals(
                listOf(listener1, listener2),
                (this as ProgressListeners).progressListenerList
            )
        }

        sketchImageView.showMaskProgressIndicator()
        val viewAbilityProgressListener = sketchImageView
            .getFieldValue<Any>("viewAbilityManager")!!
            .callMethod<Any>("getRequestListener")
        sketchImageView.getProgressListener()!!.apply {
            Assert.assertTrue(this is ProgressListeners)
            Assert.assertEquals(
                listOf(listener1, listener2, viewAbilityProgressListener),
                (this as ProgressListeners).progressListenerList
            )
        }

        sketchImageView.unregisterProgressListener(listener1)
        sketchImageView.getProgressListener()!!.apply {
            Assert.assertTrue(this is ProgressListeners)
            Assert.assertEquals(
                listOf(listener2, viewAbilityProgressListener),
                (this as ProgressListeners).progressListenerList
            )
        }

        sketchImageView.unregisterProgressListener(listener2)
        sketchImageView.getProgressListener()!!.apply {
            Assert.assertTrue(this is ProgressListeners)
            Assert.assertEquals(
                listOf(viewAbilityProgressListener),
                (this as ProgressListeners).progressListenerList
            )
        }

        sketchImageView.removeProgressIndicator()
        Assert.assertNull(sketchImageView.getProgressListener())
    }
}