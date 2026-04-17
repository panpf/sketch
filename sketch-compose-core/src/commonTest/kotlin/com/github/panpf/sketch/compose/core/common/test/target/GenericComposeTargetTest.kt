package com.github.panpf.sketch.compose.core.common.test.target

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.painter.AnimatablePainter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.OneShotRequestManager
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.target.GenericComposeTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createBitmap
import com.github.panpf.sketch.test.utils.fakeErrorImageResult
import com.github.panpf.sketch.test.utils.fakeSuccessImageResult
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
    fun testUpdatePainter() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val composeTarget = TestGenericComposeTarget()
        assertNull(composeTarget.painter)

        /*
         * disallow null image
         */
        val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri)
        val painter1 = createBitmap(100, 100).asImage().asPainter()
        withContext(Dispatchers.Main) {
            composeTarget.onStart(sketch, request, painter1.asImage())
        }
        assertSame(painter1, composeTarget.painter)

        withContext(Dispatchers.Main) {
            composeTarget.onStart(sketch, request, null)
        }
        assertSame(painter1, composeTarget.painter)

        val painter2 = createBitmap(100, 100).asImage().asPainter()
        withContext(Dispatchers.Main) {
            composeTarget.onSuccess(
                sketch,
                request,
                fakeSuccessImageResult(context),
                painter2.asImage()
            )
        }
        assertSame(painter2, composeTarget.painter)

        val painter3 = createBitmap(100, 100).asImage().asPainter()
        withContext(Dispatchers.Main) {
            composeTarget.onError(
                sketch,
                request,
                fakeErrorImageResult(context),
                painter3.asImage()
            )
        }
        assertSame(painter3, composeTarget.painter)

        withContext(Dispatchers.Main) {
            composeTarget.onError(sketch, request, fakeErrorImageResult(context), null)
        }
        assertSame(painter3, composeTarget.painter)

        /*
         * allow null image
         */
        val allowNullImageRequest = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            allowNullImage()
        }
        val painter4 = createBitmap(100, 100).asImage().asPainter()
        withContext(Dispatchers.Main) {
            composeTarget.onStart(sketch, allowNullImageRequest, painter4.asImage())
        }
        assertSame(painter4, composeTarget.painter)

        withContext(Dispatchers.Main) {
            composeTarget.onStart(sketch, allowNullImageRequest, null)
        }
        assertNull(composeTarget.painter)

        val painter5 = createBitmap(100, 100).asImage().asPainter()
        withContext(Dispatchers.Main) {
            composeTarget.onError(
                sketch,
                allowNullImageRequest,
                fakeErrorImageResult(context),
                painter5.asImage()
            )
        }
        assertSame(painter5, composeTarget.painter)

        withContext(Dispatchers.Main) {
            composeTarget.onError(
                sketch,
                allowNullImageRequest,
                fakeErrorImageResult(context),
                null
            )
        }
        assertNull(composeTarget.painter)
    }

    @Test
    fun testAnimatablePainter() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, null)

        TestGenericComposeTarget().apply {
            val painter = ColorPainter(Color.Red)
            onStart(sketch, request, painter.asImage())
            onError(sketch, request, fakeErrorImageResult(context), painter.asImage())
            onSuccess(sketch, request, fakeSuccessImageResult(context), painter.asImage())

            onStateChanged(GlobalLifecycle.owner, Lifecycle.Event.ON_START)

            onStart(sketch, request, painter.asImage())
            onError(sketch, request, fakeErrorImageResult(context), painter.asImage())
            onSuccess(sketch, request, fakeSuccessImageResult(context), painter.asImage())
        }

        TestGenericComposeTarget().apply {
            assertFalse(isStarted)
            assertFalse(isAttached)

            val animatableDrawable = TestAnimatableColorPainter(Color.Red)
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
                ColorPainter(Color.Red).asImage()
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

class TestGenericComposeTarget constructor(
    override val contentScale: ContentScale = ContentScale.Fit,
    override val alignment: Alignment = Alignment.Center
) : GenericComposeTarget() {

    private var _painter: Painter? = null

    private val requestManager = OneShotRequestManager()

    override val painter: Painter?
        get() = _painter

    override fun setPainter(painter: Painter?) {
        this._painter = painter
    }

    override fun getRequestManager(): RequestManager {
        return requestManager
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestGenericComposeTarget
        if (contentScale != other.contentScale) return false
        return true
    }

    override fun hashCode(): Int {
        return contentScale.hashCode()
    }

    override fun toString(): String {
        return "TestComposeTarget(contentScale=$contentScale)"
    }
}