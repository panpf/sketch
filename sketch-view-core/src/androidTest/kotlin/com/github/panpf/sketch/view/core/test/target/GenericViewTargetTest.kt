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
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.target.GenericViewTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContextSync
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class GenericViewTargetTest {

    // TODO test allowSetNullDrawable

    @Test
    fun testUpdateDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            allowNullImage()
        }
        val requestContext = request.toRequestContextSync(sketch)

        val imageView = ImageView(context)
        assertNull(imageView.drawable)

        val imageViewTarget = TestViewTarget(imageView)
        assertNull(imageViewTarget.drawable)

        val drawable1 = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))
        val drawable2 = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onSuccess(requestContext, drawable1.asSketchImage())
        }

        assertSame(drawable1, imageView.drawable)
        assertSame(drawable1, imageViewTarget.drawable)

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onSuccess(requestContext, drawable2.asSketchImage())
        }

        assertSame(drawable2, imageView.drawable)
        assertSame(drawable2, imageViewTarget.drawable)

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onError(requestContext, null)
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
    fun testAnimatableDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val imageView = ImageView(context)
        val requestContext = RequestContext(sketch, ImageRequest(context, null))

        TestViewTarget(imageView).apply {
            val drawable = ColorDrawable(Color.RED)
            onStart(requestContext, drawable.asSketchImage())
            onError(requestContext, drawable.asSketchImage())
            onSuccess(requestContext, drawable.asSketchImage())

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)

            onStart(requestContext, drawable.asSketchImage())
            onError(requestContext, drawable.asSketchImage())
            onSuccess(requestContext, drawable.asSketchImage())
        }

        TestViewTarget(imageView).apply {
            assertFalse(getFieldValue<Boolean>("isStarted")!!)
            assertFalse(getFieldValue<Boolean>("isAttached")!!)

            val animatableDrawable = TestAnimatableColorDrawable(Color.RED)
            assertFalse(animatableDrawable.running)

            onSuccess(requestContext, animatableDrawable.asSketchImage())
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

            onSuccess(requestContext, ColorDrawable(Color.RED).asSketchImage())
            assertFalse(animatableDrawable.running)

            onSuccess(requestContext, animatableDrawable.asSketchImage())
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