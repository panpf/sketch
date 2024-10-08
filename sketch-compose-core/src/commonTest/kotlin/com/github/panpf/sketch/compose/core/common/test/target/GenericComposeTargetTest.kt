package com.github.panpf.sketch.compose.core.common.test.target

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.painter.AnimatablePainter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.TestGenericComposeTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GenericComposeTargetTest {

    @Test
    fun testIsStarted() {
        TestGenericComposeTarget().apply {
            assertFalse(isStarted)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)
            assertTrue(isStarted)

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_STOP)
            assertFalse(isStarted)
        }
    }

    @Test
    fun testIsAttached() {
        TestGenericComposeTarget().apply {
            assertFalse(isAttached)

            onAttachedChanged(true)
            assertTrue(isAttached)

            onAttachedChanged(false)
            assertFalse(isAttached)
        }
    }

    @Test
    fun testUpdateDrawable() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val imageViewTarget = TestGenericComposeTarget()
        assertNull(imageViewTarget.painter)

        /*
         * disallow null image
         */
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val painter1 = createBitmap(100, 100).asImage().asPainter()
        withContext(Dispatchers.Main) {
            imageViewTarget.onStart(sketch, request, painter1.asImage())
        }
        assertSame(painter1, imageViewTarget.painter)

        withContext(Dispatchers.Main) {
            imageViewTarget.onStart(sketch, request, null)
        }
        assertSame(painter1, imageViewTarget.painter)

        val painter2 = createBitmap(100, 100).asImage().asPainter()
        withContext(Dispatchers.Main) {
            imageViewTarget.onSuccess(sketch, request, painter2.asImage())
        }
        assertSame(painter2, imageViewTarget.painter)

        val painter3 = createBitmap(100, 100).asImage().asPainter()
        withContext(Dispatchers.Main) {
            imageViewTarget.onError(sketch, request, painter3.asImage())
        }
        assertSame(painter3, imageViewTarget.painter)

        withContext(Dispatchers.Main) {
            imageViewTarget.onError(sketch, request, null)
        }
        assertSame(painter3, imageViewTarget.painter)

        /*
         * allow null image
         */
        val allowNullImageRequest = ImageRequest(context, ResourceImages.jpeg.uri) {
            allowNullImage()
        }
        val painter4 = createBitmap(100, 100).asImage().asPainter()
        withContext(Dispatchers.Main) {
            imageViewTarget.onStart(sketch, allowNullImageRequest, painter4.asImage())
        }
        assertSame(painter4, imageViewTarget.painter)

        withContext(Dispatchers.Main) {
            imageViewTarget.onStart(sketch, allowNullImageRequest, null)
        }
        assertNull(imageViewTarget.painter)

        val painter5 = createBitmap(100, 100).asImage().asPainter()
        withContext(Dispatchers.Main) {
            imageViewTarget.onError(sketch, allowNullImageRequest, painter5.asImage())
        }
        assertSame(painter5, imageViewTarget.painter)

        withContext(Dispatchers.Main) {
            imageViewTarget.onError(sketch, allowNullImageRequest, null)
        }
        assertNull(imageViewTarget.painter)
    }

    @Test
    fun testAnimatableDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, null)

        TestGenericComposeTarget().apply {
            val painter = ColorPainter(Color.Red)
            onStart(sketch, request, painter.asImage())
            onError(sketch, request, painter.asImage())
            onSuccess(sketch, request, painter.asImage())

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)

            onStart(sketch, request, painter.asImage())
            onError(sketch, request, painter.asImage())
            onSuccess(sketch, request, painter.asImage())
        }

        TestGenericComposeTarget().apply {
            assertFalse(isStarted)
            assertFalse(isAttached)

            val animatableDrawable = TestAnimatableColorPainter(Color.Red)
            assertFalse(animatableDrawable.running)

            onSuccess(sketch, request, animatableDrawable.asImage())
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

            onSuccess(sketch, request, ColorPainter(Color.Red).asImage())
            assertFalse(animatableDrawable.running)

            onSuccess(sketch, request, animatableDrawable.asImage())
            assertTrue(animatableDrawable.running)
        }
    }

    class TestAnimatableColorPainter(color: Color) : PainterWrapper(ColorPainter(color)),
        AnimatablePainter {
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