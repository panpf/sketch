package com.github.panpf.sketch.extensions.view.test

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Build
import android.view.LayoutInflater
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.ability.removeProgressIndicator
import com.github.panpf.sketch.ability.setClickIgnoreSaveCellularTrafficEnabled
import com.github.panpf.sketch.ability.showMaskProgressIndicator
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.request.internal.Listeners
import com.github.panpf.sketch.request.internal.PairListener
import com.github.panpf.sketch.request.internal.PairProgressListener
import com.github.panpf.sketch.request.internal.ProgressListeners
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.tools4j.reflect.ktx.callMethod
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SketchImageViewTest {

    @Test
    fun testAttrs() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_default, null, false) as SketchImageView).apply {
            assertNull(imageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test, null, false) as SketchImageView).apply {
            assertEquals(ImageOptions {
                colorType(Bitmap.Config.RGB_565)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    colorSpace(ColorSpace.Named.LINEAR_SRGB)
                }
                crossfade(3000, preferExactIntrinsicSize = true)
                depth(Depth.LOCAL)
                downloadCachePolicy(CachePolicy.WRITE_ONLY)
                memoryCachePolicy(CachePolicy.DISABLED)
                @Suppress("DEPRECATION")
                preferQualityOverSpeed()
                resizeOnDraw()
                resize(354, 2789, Precision.SAME_ASPECT_RATIO, Scale.FILL)
                resultCachePolicy(CachePolicy.READ_ONLY)
                transformations(RoundedCornersTransformation(200f))
            }, imageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_state, null, false) as SketchImageView).apply {
            assertNotNull(imageOptions!!.placeholder)
            assertNotNull(imageOptions!!.fallback)
            assertNotNull(imageOptions!!.error)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_blur, null, false) as SketchImageView).apply {
            assertEquals(ImageOptions {
                transformations(
                    BlurTransformation(
                        23,
                        hasAlphaBitmapBgColor = Color.parseColor("#0000FF"),
                        maskColor = Color.parseColor("#00FF00")
                    )
                )
            }, imageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_rotate, null, false) as SketchImageView).apply {
            assertEquals(ImageOptions {
                transformations(RotateTransformation(444))
            }, imageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_circle, null, false) as SketchImageView).apply {
            assertEquals(ImageOptions {
                transformations(CircleCropTransformation(Scale.END_CROP))
            }, imageOptions)
        }

        (LayoutInflater.from(context)
            .inflate(R.layout.attrs_test_mask, null, false) as SketchImageView).apply {
            assertEquals(ImageOptions {
                transformations(MaskTransformation(Color.parseColor("#00FF00")))
            }, imageOptions)
        }
    }

    @Test
    fun testListener() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketchImageView = SketchImageView(context)

        sketchImageView.getListener().apply {
            assertTrue(this is Listeners)
            assertEquals(
                listOf(sketchImageView.requestState),
                list
            )
        }

        val listener1 = object : Listener {}
        val listener2 = object : Listener {}

        sketchImageView.registerListener(listener1)
        sketchImageView.getListener()!!.apply {
            assertTrue(this is Listeners)
            assertEquals(
                listOf(sketchImageView.requestState, listener1),
                list
            )
        }

        sketchImageView.unregisterListener(listener2)
        sketchImageView.getListener()!!.apply {
            assertTrue(this is Listeners)
            assertEquals(
                listOf(sketchImageView.requestState, listener1),
                list
            )
        }

        sketchImageView.registerListener(listener2)
        sketchImageView.getListener()!!.apply {
            assertTrue(this is Listeners)
            assertEquals(
                listOf(sketchImageView.requestState, listener1, listener2),
                list
            )
        }

        sketchImageView.setClickIgnoreSaveCellularTrafficEnabled(true)
        val viewAbilityListener = sketchImageView
            .getFieldValue<Any>("viewAbilityManager")!!
            .callMethod<Any>("getRequestListener")
        sketchImageView.getListener()!!.apply {
            assertTrue(this is PairListener)
            assertEquals(
                listOf(sketchImageView.requestState, listener1, listener2),
                first.asOrThrow<Listeners>().list
            )
            assertSame(
                viewAbilityListener,
                this.second
            )
        }

        sketchImageView.unregisterListener(listener1)
        sketchImageView.getListener()!!.apply {
            assertTrue(this is PairListener)
            assertEquals(
                listOf(sketchImageView.requestState, listener2),
                first.asOrThrow<Listeners>().list
            )
            assertSame(
                viewAbilityListener,
                this.second
            )
        }

        sketchImageView.unregisterListener(listener2)
        sketchImageView.getListener()!!.apply {
            assertTrue(this is PairListener)
            assertEquals(
                listOf(sketchImageView.requestState),
                first.asOrThrow<Listeners>().list
            )
            assertSame(
                viewAbilityListener,
                this.second
            )
        }

        sketchImageView.setClickIgnoreSaveCellularTrafficEnabled(false)
        sketchImageView.getListener().apply {
            assertTrue(this is Listeners)
            assertEquals(
                listOf(sketchImageView.requestState),
                list
            )
        }
    }

    @Test
    fun testProgressListener() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketchImageView = SketchImageView(context)

        sketchImageView.getProgressListener().apply {
            assertTrue(this is ProgressListeners)
            assertEquals(
                listOf(sketchImageView.requestState),
                list
            )
        }

        val listener1 = ProgressListener { _, _ -> }
        val listener2 = ProgressListener { _, _ -> }

        sketchImageView.registerProgressListener(listener1)
        sketchImageView.getProgressListener()!!.apply {
            assertTrue(this is ProgressListeners)
            assertEquals(
                listOf(sketchImageView.requestState, listener1),
                list
            )
        }

        sketchImageView.unregisterProgressListener(listener2)
        sketchImageView.getProgressListener()!!.apply {
            assertTrue(this is ProgressListeners)
            assertEquals(
                listOf(sketchImageView.requestState, listener1),
                list
            )
        }

        sketchImageView.registerProgressListener(listener2)
        sketchImageView.getProgressListener()!!.apply {
            assertTrue(this is ProgressListeners)
            assertEquals(
                listOf(sketchImageView.requestState, listener1, listener2),
                list
            )
        }

        sketchImageView.showMaskProgressIndicator()
        val viewAbilityProgressListener = sketchImageView
            .getFieldValue<Any>("viewAbilityManager")!!
            .callMethod<Any>("getRequestListener")
        sketchImageView.getProgressListener()!!.apply {
            assertTrue(this is PairProgressListener)
            assertEquals(
                listOf(sketchImageView.requestState, listener1, listener2),
                first.asOrThrow<ProgressListeners>().list
            )
            assertSame(
                viewAbilityProgressListener,
                this.second
            )
        }

        sketchImageView.unregisterProgressListener(listener1)
        sketchImageView.getProgressListener()!!.apply {
            assertTrue(this is PairProgressListener)
            assertEquals(
                listOf(sketchImageView.requestState, listener2),
                first.asOrThrow<ProgressListeners>().list
            )
            assertSame(
                viewAbilityProgressListener,
                this.second
            )
        }

        sketchImageView.unregisterProgressListener(listener2)
        sketchImageView.getProgressListener()!!.apply {
            assertTrue(this is PairProgressListener)
            assertEquals(
                listOf(sketchImageView.requestState),
                first.asOrThrow<ProgressListeners>().list
            )
            assertSame(
                viewAbilityProgressListener,
                this.second
            )
        }

        sketchImageView.removeProgressIndicator()
        sketchImageView.getProgressListener().apply {
            assertTrue(this is ProgressListeners)
            assertEquals(
                listOf(sketchImageView.requestState),
                list
            )
        }
    }
}