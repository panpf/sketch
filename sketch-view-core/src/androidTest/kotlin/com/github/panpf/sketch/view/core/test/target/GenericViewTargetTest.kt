package com.github.panpf.sketch.view.core.test.target

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.target.GlobalTargetLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.allowNullImage
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.target.GenericViewTarget
import com.github.panpf.sketch.target.TargetLifecycle
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContextSync
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
        val request = ImageRequest(context, MyImages.jpeg.uri) {
            allowNullImage()
        }
        val requestContext = request.toRequestContextSync(sketch)

        val imageView = ImageView(context)
        Assert.assertNull(imageView.drawable)

        val imageViewTarget = TestViewTarget(imageView)
        Assert.assertNull(imageViewTarget.drawable)

        val drawable1 = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))
        val drawable2 = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onSuccess(requestContext, drawable1.asSketchImage())
        }

        Assert.assertSame(drawable1, imageView.drawable)
        Assert.assertSame(drawable1, imageViewTarget.drawable)

        runBlocking(Dispatchers.Main) {
            imageViewTarget.onSuccess(requestContext, drawable2.asSketchImage())
        }

        Assert.assertSame(drawable2, imageView.drawable)
        Assert.assertSame(drawable2, imageViewTarget.drawable)

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

            onStateChanged(GlobalTargetLifecycle, TargetLifecycle.Event.ON_START)
            Assert.assertTrue(getFieldValue<Boolean>("isStarted")!!)

            onStateChanged(GlobalTargetLifecycle, TargetLifecycle.Event.ON_STOP)
            Assert.assertFalse(getFieldValue<Boolean>("isStarted")!!)
        }
    }

    @Test
    fun testIsAttached() {
        val context = getTestContext()
        val imageView = ImageView(context)

        TestViewTarget(imageView).apply {
            Assert.assertFalse(getFieldValue<Boolean>("isAttached")!!)

            onAttachedChanged(true)
            Assert.assertTrue(getFieldValue<Boolean>("isAttached")!!)

            onAttachedChanged(false)
            Assert.assertFalse(getFieldValue<Boolean>("isAttached")!!)
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

            onStateChanged(GlobalTargetLifecycle, TargetLifecycle.Event.ON_START)

            onStart(requestContext, drawable.asSketchImage())
            onError(requestContext, drawable.asSketchImage())
            onSuccess(requestContext, drawable.asSketchImage())
        }

        TestViewTarget(imageView).apply {
            Assert.assertFalse(getFieldValue<Boolean>("isStarted")!!)
            Assert.assertFalse(getFieldValue<Boolean>("isAttached")!!)

            val animatableDrawable = TestAnimatableColorDrawable(Color.RED)
            Assert.assertFalse(animatableDrawable.running)

            onSuccess(requestContext, animatableDrawable.asSketchImage())
            Assert.assertFalse(getFieldValue<Boolean>("isStarted")!!)
            Assert.assertFalse(getFieldValue<Boolean>("isAttached")!!)
            Assert.assertFalse(animatableDrawable.running)

            onStateChanged(GlobalTargetLifecycle, TargetLifecycle.Event.ON_START)
            Assert.assertTrue(getFieldValue<Boolean>("isStarted")!!)
            Assert.assertFalse(getFieldValue<Boolean>("isAttached")!!)
            Assert.assertFalse(animatableDrawable.running)

            onAttachedChanged(true)
            Assert.assertTrue(getFieldValue<Boolean>("isStarted")!!)
            Assert.assertTrue(getFieldValue<Boolean>("isAttached")!!)
            Assert.assertTrue(animatableDrawable.running)

            onStateChanged(GlobalTargetLifecycle, TargetLifecycle.Event.ON_STOP)
            Assert.assertFalse(getFieldValue<Boolean>("isStarted")!!)
            Assert.assertTrue(getFieldValue<Boolean>("isAttached")!!)
            Assert.assertFalse(animatableDrawable.running)

            onStateChanged(GlobalTargetLifecycle, TargetLifecycle.Event.ON_START)
            Assert.assertTrue(getFieldValue<Boolean>("isStarted")!!)
            Assert.assertTrue(getFieldValue<Boolean>("isAttached")!!)
            Assert.assertTrue(animatableDrawable.running)

            onAttachedChanged(false)
            Assert.assertTrue(getFieldValue<Boolean>("isStarted")!!)
            Assert.assertFalse(getFieldValue<Boolean>("isAttached")!!)
            Assert.assertFalse(animatableDrawable.running)

            onAttachedChanged(true)
            Assert.assertTrue(getFieldValue<Boolean>("isStarted")!!)
            Assert.assertTrue(getFieldValue<Boolean>("isAttached")!!)
            Assert.assertTrue(animatableDrawable.running)

            onSuccess(requestContext, ColorDrawable(Color.RED).asSketchImage())
            Assert.assertFalse(animatableDrawable.running)

            onSuccess(requestContext, animatableDrawable.asSketchImage())
            Assert.assertTrue(animatableDrawable.running)
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