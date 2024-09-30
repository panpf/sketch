package com.github.panpf.sketch.view.core.test.target

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.GenericViewTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class GenericViewTargetTest {

    @Test
    fun testUpdateDrawable() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val imageView = ImageView(context)
        val imageViewTarget = TestViewTarget(imageView)
        assertNull(imageView.drawable)
        assertNull(imageViewTarget.drawable)

        /*
         * disallow null image
         */
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val drawable1 = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))
        withContext(Dispatchers.Main) {
            imageViewTarget.onStart(sketch, request, drawable1.asImage())
        }
        assertSame(drawable1, imageView.drawable)
        assertSame(drawable1, imageViewTarget.drawable)

        withContext(Dispatchers.Main) {
            imageViewTarget.onStart(sketch, request, null)
        }
        assertSame(drawable1, imageView.drawable)
        assertSame(drawable1, imageViewTarget.drawable)

        val drawable2 = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))
        withContext(Dispatchers.Main) {
            imageViewTarget.onSuccess(sketch, request, drawable2.asImage())
        }
        assertSame(drawable2, imageView.drawable)
        assertSame(drawable2, imageViewTarget.drawable)

        val drawable3 = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))
        withContext(Dispatchers.Main) {
            imageViewTarget.onError(sketch, request, drawable3.asImage())
        }
        assertSame(drawable3, imageView.drawable)
        assertSame(drawable3, imageViewTarget.drawable)

        withContext(Dispatchers.Main) {
            imageViewTarget.onError(sketch, request, null)
        }
        assertSame(drawable3, imageView.drawable)
        assertSame(drawable3, imageViewTarget.drawable)

        /*
         * allow null image
         */
        val allowNullImageRequest = ImageRequest(context, ResourceImages.jpeg.uri) {
            allowNullImage()
        }
        val drawable4 = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))
        withContext(Dispatchers.Main) {
            imageViewTarget.onStart(sketch, allowNullImageRequest, drawable4.asImage())
        }
        assertSame(drawable4, imageView.drawable)
        assertSame(drawable4, imageViewTarget.drawable)

        withContext(Dispatchers.Main) {
            imageViewTarget.onStart(sketch, allowNullImageRequest, null)
        }
        assertNull(imageView.drawable)
        assertNull(imageViewTarget.drawable)

        val drawable5 = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))
        withContext(Dispatchers.Main) {
            imageViewTarget.onError(sketch, allowNullImageRequest, drawable5.asImage())
        }
        assertSame(drawable5, imageView.drawable)
        assertSame(drawable5, imageViewTarget.drawable)

        withContext(Dispatchers.Main) {
            imageViewTarget.onError(sketch, allowNullImageRequest, null)
        }
        assertNull(imageView.drawable)
        assertNull(imageViewTarget.drawable)
    }

    @Test
    fun testIsStarted() {
        val context = getTestContext()
        val imageView = ImageView(context)

        TestViewTarget(imageView).apply {
            assertFalse(getFieldValue<Boolean>("isStarted")!!)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)
            assertTrue(getFieldValue<Boolean>("isStarted")!!)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_STOP)
            assertFalse(getFieldValue<Boolean>("isStarted")!!)
        }
    }

    @Test
    fun testIsAttached() {
        val context = getTestContext()
        val imageView = ImageView(context)

        TestViewTarget(imageView).apply {
            assertFalse(getFieldValue<Boolean>("isAttached")!!)

            onAttachedChanged(true)
            assertTrue(getFieldValue<Boolean>("isAttached")!!)

            onAttachedChanged(false)
            assertFalse(getFieldValue<Boolean>("isAttached")!!)
        }
    }

    @Test
    fun testAnimatableDrawableAndLifecycle() {
        val (context, sketch) = getTestContextAndSketch()
        val imageView = ImageView(context)
        val request = ImageRequest(context, null)

        TestViewTarget(imageView).apply {
            val drawable = ColorDrawable(Color.RED)
            onStart(sketch, request, drawable.asImage())
            onError(sketch, request, drawable.asImage())
            onSuccess(sketch, request, drawable.asImage())

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)

            onStart(sketch, request, drawable.asImage())
            onError(sketch, request, drawable.asImage())
            onSuccess(sketch, request, drawable.asImage())
        }

        TestViewTarget(imageView).apply {
            assertFalse(getFieldValue<Boolean>("isStarted")!!)
            assertFalse(getFieldValue<Boolean>("isAttached")!!)

            val animatableDrawable = TestAnimatableColorDrawable(Color.RED)
            assertFalse(animatableDrawable.running)

            onSuccess(sketch, request, animatableDrawable.asImage())
            assertFalse(getFieldValue<Boolean>("isStarted")!!)
            assertFalse(getFieldValue<Boolean>("isAttached")!!)
            assertFalse(animatableDrawable.running)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)
            assertTrue(getFieldValue<Boolean>("isStarted")!!)
            assertFalse(getFieldValue<Boolean>("isAttached")!!)
            assertFalse(animatableDrawable.running)

            onAttachedChanged(true)
            assertTrue(getFieldValue<Boolean>("isStarted")!!)
            assertTrue(getFieldValue<Boolean>("isAttached")!!)
            assertTrue(animatableDrawable.running)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_STOP)
            assertFalse(getFieldValue<Boolean>("isStarted")!!)
            assertTrue(getFieldValue<Boolean>("isAttached")!!)
            assertFalse(animatableDrawable.running)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)
            assertTrue(getFieldValue<Boolean>("isStarted")!!)
            assertTrue(getFieldValue<Boolean>("isAttached")!!)
            assertTrue(animatableDrawable.running)

            onAttachedChanged(false)
            assertTrue(getFieldValue<Boolean>("isStarted")!!)
            assertFalse(getFieldValue<Boolean>("isAttached")!!)
            assertFalse(animatableDrawable.running)

            onAttachedChanged(true)
            assertTrue(getFieldValue<Boolean>("isStarted")!!)
            assertTrue(getFieldValue<Boolean>("isAttached")!!)
            assertTrue(animatableDrawable.running)

            onSuccess(sketch, request, ColorDrawable(Color.RED).asImage())
            assertFalse(animatableDrawable.running)

            onSuccess(sketch, request, animatableDrawable.asImage())
            assertTrue(animatableDrawable.running)
        }
    }

    class TestViewTarget(override val view: ImageView) : GenericViewTarget<ImageView>(view) {

        override var drawable: Drawable?
            get() = view.drawable
            set(value) {
                view.setImageDrawable(value)
            }

        override val fitScale: Boolean
            get() = view.scaleType.fitScale
    }

    class TestAnimatableColorDrawable(color: Int) : ColorDrawable(color), Animatable {
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