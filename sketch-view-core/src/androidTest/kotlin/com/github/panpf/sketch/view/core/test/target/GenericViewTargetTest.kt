package com.github.panpf.sketch.view.core.test.target

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.fakeErrorImageResult
import com.github.panpf.sketch.test.utils.fakeSuccessImageResult
import com.github.panpf.sketch.test.utils.getTestContext
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
    fun testIsStarted() {
        val context = getTestContext()
        val imageView = ImageView(context)

        TestGenericViewTarget(imageView).apply {
            assertFalse(isStarted)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)
            assertTrue(isStarted)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_STOP)
            assertFalse(isStarted)
        }
    }

    @Test
    fun testIsAttached() {
        val context = getTestContext()
        val imageView = ImageView(context)

        TestGenericViewTarget(imageView).apply {
            assertFalse(isAttached)

            onAttachedChanged(true)
            assertTrue(isAttached)

            onAttachedChanged(false)
            assertFalse(isAttached)
        }
    }

    @Test
    fun testGetRequestManager() {
        val context = getTestContext()
        val imageView = ImageView(context)

        assertSame(
            expected = imageView.requestManager,
            actual = TestGenericViewTarget(imageView).getRequestManager()
        )
    }

    @Test
    fun testUpdateDrawable() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val imageView = ImageView(context)
        val imageViewTarget = TestGenericViewTarget(imageView)
        assertNull(imageView.drawable)
        assertNull(imageViewTarget.drawable)

        /*
         * disallow null image
         */
        val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri)
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
            imageViewTarget.onSuccess(
                sketch,
                request,
                fakeSuccessImageResult(context),
                drawable2.asImage()
            )
        }
        assertSame(drawable2, imageView.drawable)
        assertSame(drawable2, imageViewTarget.drawable)

        val drawable3 = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))
        withContext(Dispatchers.Main) {
            imageViewTarget.onError(
                sketch,
                request,
                fakeErrorImageResult(context),
                drawable3.asImage()
            )
        }
        assertSame(drawable3, imageView.drawable)
        assertSame(drawable3, imageViewTarget.drawable)

        withContext(Dispatchers.Main) {
            imageViewTarget.onError(sketch, request, fakeErrorImageResult(context), null)
        }
        assertSame(drawable3, imageView.drawable)
        assertSame(drawable3, imageViewTarget.drawable)

        /*
         * allow null image
         */
        val allowNullImageRequest = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
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
            imageViewTarget.onError(
                sketch,
                allowNullImageRequest,
                fakeErrorImageResult(context),
                drawable5.asImage()
            )
        }
        assertSame(drawable5, imageView.drawable)
        assertSame(drawable5, imageViewTarget.drawable)

        withContext(Dispatchers.Main) {
            imageViewTarget.onError(
                sketch,
                allowNullImageRequest,
                fakeErrorImageResult(context),
                null
            )
        }
        assertNull(imageView.drawable)
        assertNull(imageViewTarget.drawable)
    }

    @Test
    fun testAnimatableDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val imageView = ImageView(context)
        val request = ImageRequest(context, null)

        TestGenericViewTarget(imageView).apply {
            val drawable = ColorDrawable(Color.RED)
            onStart(sketch, request, drawable.asImage())
            onError(sketch, request, fakeErrorImageResult(context), drawable.asImage())
            onSuccess(sketch, request, fakeSuccessImageResult(context), drawable.asImage())

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)

            onStart(sketch, request, drawable.asImage())
            onError(sketch, request, fakeErrorImageResult(context), drawable.asImage())
            onSuccess(sketch, request, fakeSuccessImageResult(context), drawable.asImage())
        }

        TestGenericViewTarget(imageView).apply {
            assertFalse(isStarted)
            assertFalse(isAttached)

            val animatableDrawable = TestAnimatableColorDrawable(Color.RED)
            assertFalse(animatableDrawable.running)

            onSuccess(
                sketch,
                request,
                fakeSuccessImageResult(context),
                animatableDrawable.asImage()
            )
            assertFalse(isStarted)
            assertFalse(isAttached)
            assertFalse(animatableDrawable.running)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)
            assertTrue(isStarted)
            assertFalse(isAttached)
            assertFalse(animatableDrawable.running)

            onAttachedChanged(true)
            assertTrue(isStarted)
            assertTrue(isAttached)
            assertTrue(animatableDrawable.running)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_STOP)
            assertFalse(isStarted)
            assertTrue(isAttached)
            assertFalse(animatableDrawable.running)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)
            assertTrue(isStarted)
            assertTrue(isAttached)
            assertTrue(animatableDrawable.running)

            onAttachedChanged(false)
            assertTrue(isStarted)
            assertFalse(isAttached)
            assertFalse(animatableDrawable.running)

            onAttachedChanged(true)
            assertTrue(isStarted)
            assertTrue(isAttached)
            assertTrue(animatableDrawable.running)

            onSuccess(
                sketch,
                request,
                fakeSuccessImageResult(context),
                ColorDrawable(Color.RED).asImage()
            )
            assertFalse(animatableDrawable.running)

            onSuccess(
                sketch,
                request,
                fakeSuccessImageResult(context),
                animatableDrawable.asImage()
            )
            assertTrue(animatableDrawable.running)
        }
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