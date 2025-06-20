package com.github.panpf.sketch.compose.core.common.test.target

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.painter.ImageBitmapPainter
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.target.AsyncImageTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.RememberedPainter
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.test.utils.fakeErrorImageResult
import com.github.panpf.sketch.test.utils.fakeSuccessImageResult
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class AsyncImageTargetTest {

    @Test
    fun testPainterAndRemembered() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)
        assertEquals(expected = null, actual = state.painterState)
        assertEquals(expected = null, actual = target.painter)

        val painter1 = ColorPainter(Color.Red)
        val painter2 = RememberedPainter(ColorPainter(Color.Green))
        val painter3 = ColorPainter(Color.Yellow)
        val painter4 = RememberedPainter(ColorPainter(Color.Blue))

        target.onSuccess(sketch, request, fakeSuccessImageResult(context), painter1.asImage())
        assertSame(expected = painter1, actual = state.painter)
        assertSame(expected = painter1, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 0, actual = painter4.rememberedCounter.count)

        target.onSuccess(sketch, request, fakeSuccessImageResult(context), painter2.asImage())
        assertSame(expected = painter2, actual = state.painter)
        assertSame(expected = painter2, actual = target.painter)
        assertEquals(expected = 1, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 0, actual = painter4.rememberedCounter.count)

        target.onSuccess(sketch, request, fakeSuccessImageResult(context), painter3.asImage())
        assertSame(expected = painter3, actual = state.painter)
        assertSame(expected = painter3, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 0, actual = painter4.rememberedCounter.count)

        target.onSuccess(sketch, request, fakeSuccessImageResult(context), painter4.asImage())
        assertSame(expected = painter4, actual = state.painter)
        assertSame(expected = painter4, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 1, actual = painter4.rememberedCounter.count)

        target.onForgotten()
        assertSame(expected = painter4, actual = state.painter)
        assertSame(expected = painter4, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 0, actual = painter4.rememberedCounter.count)

        target.onRemembered()
        assertSame(expected = painter4, actual = state.painter)
        assertSame(expected = painter4, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 1, actual = painter4.rememberedCounter.count)

        target.onForgotten()
        assertSame(expected = painter4, actual = state.painter)
        assertSame(expected = painter4, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 0, actual = painter4.rememberedCounter.count)
    }

    @Test
    fun testPreviewImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)
        assertEquals(expected = null, actual = state.painter)
        assertEquals(expected = null, actual = target.painter)
        assertEquals(expected = null, actual = state.painterState)
        assertEquals(expected = null, actual = state.loadState)

        val previewPainter = ColorPainter(Color.Red)

        target.setPreviewImage(sketch, request, previewPainter.asImage())
        assertEquals(expected = previewPainter, actual = state.painter)
        assertEquals(expected = previewPainter, actual = target.painter)
        assertEquals(
            expected = PainterState.Loading(previewPainter),
            actual = state.painterState
        )
        assertEquals(
            expected = LoadState.Started(request),
            actual = state.loadState
        )
    }

    @Test
    fun testContentScaleAndAlignment() {
        val context = getTestContext()
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)

        assertEquals(expected = ContentScale.Fit, actual = target.contentScale)
        assertEquals(expected = Alignment.Center, actual = target.alignment)
        assertEquals(expected = true, actual = target.fitScale)


        state.contentScale = ContentScale.Inside
        assertEquals(expected = ContentScale.Inside, actual = target.contentScale)

        state.contentScale = ContentScale.None
        assertEquals(expected = ContentScale.None, actual = target.contentScale)


        state.alignment = Alignment.TopStart
        assertEquals(expected = Alignment.TopStart, actual = target.alignment)

        state.alignment = Alignment.BottomEnd
        assertEquals(expected = Alignment.BottomEnd, actual = target.alignment)


        state.contentScale = ContentScale.Inside
        assertEquals(expected = true, actual = target.fitScale)

        state.contentScale = ContentScale.Fit
        assertEquals(expected = true, actual = target.fitScale)

        state.contentScale = ContentScale.Crop
        assertEquals(expected = false, actual = target.fitScale)

        state.contentScale = ContentScale.None
        assertEquals(expected = false, actual = target.fitScale)

        state.contentScale = ContentScale.FillWidth
        assertEquals(expected = false, actual = target.fitScale)

        state.contentScale = ContentScale.FillHeight
        assertEquals(expected = false, actual = target.fitScale)

        state.contentScale = ContentScale.FillBounds
        assertEquals(expected = false, actual = target.fitScale)
    }

    @Test
    fun testFilterQuality() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)
        assertEquals(expected = FilterQuality.Low, actual = target.filterQuality)
        target.onSuccess(
            sketch = sketch,
            request = request,
            result = fakeSuccessImageResult(context),
            image = createBitmapImage(101, 202)
        ).apply {
            assertEquals(
                expected = FilterQuality.Low,
                actual = target.painter!!.asOrThrow<ImageBitmapPainter>().filterQuality
            )
        }

        state.filterQuality = FilterQuality.High
        assertEquals(expected = FilterQuality.High, actual = target.filterQuality)
        target.onSuccess(
            sketch = sketch,
            request = request,
            result = fakeSuccessImageResult(context),
            image = createBitmapImage(101, 202)
        ).apply {
            assertEquals(
                expected = FilterQuality.High,
                actual = target.painter!!.asOrThrow<ImageBitmapPainter>().filterQuality
            )
        }
    }

    @Test
    fun testRequestManagerAndRemembered() {
        val context = getTestContext()
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)
        assertSame(expected = target.getRequestManager(), actual = target.getRequestManager())
    }

    @Test
    fun testListenerAndProgressListener() {
        val context = getTestContext()
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)
        assertSame(expected = target.getListener(), actual = target.getListener())
        assertSame(expected = target.getProgressListener(), actual = target.getProgressListener())
    }

    @Test
    fun testLifecycleResolver() {
        val context = getTestContext()
        val lifecycle = TestLifecycle()
        val state = AsyncImageState(context, false, lifecycle, null)
        val target = AsyncImageTarget(state)
        assertEquals(
            expected = LifecycleResolver(lifecycle),
            actual = target.getLifecycleResolver()
        )
    }

    @Test
    fun testSizeResolver() {
        val context = getTestContext()
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)
        assertSame(
            expected = state.sizeResolver,
            actual = target.getSizeResolver()
        )
    }

    @Test
    fun testScaleDecider() {
        val context = getTestContext()
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())

        state.contentScale = ContentScale.Fit
        state.alignment = Alignment.TopStart
        assertEquals(expected = ScaleDecider(Scale.START_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.TopCenter
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.TopEnd
        assertEquals(expected = ScaleDecider(Scale.END_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.CenterStart
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.Center
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.CenterEnd
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomStart
        assertEquals(expected = ScaleDecider(Scale.START_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomCenter
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomEnd
        assertEquals(expected = ScaleDecider(Scale.END_CROP), actual = target.getScaleDecider())

        state.contentScale = ContentScale.Crop
        state.alignment = Alignment.TopStart
        assertEquals(expected = ScaleDecider(Scale.START_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.TopCenter
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.TopEnd
        assertEquals(expected = ScaleDecider(Scale.END_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.CenterStart
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.Center
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.CenterEnd
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomStart
        assertEquals(expected = ScaleDecider(Scale.START_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomCenter
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomEnd
        assertEquals(expected = ScaleDecider(Scale.END_CROP), actual = target.getScaleDecider())

        state.contentScale = ContentScale.None
        state.alignment = Alignment.TopStart
        assertEquals(expected = ScaleDecider(Scale.START_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.TopCenter
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.TopEnd
        assertEquals(expected = ScaleDecider(Scale.END_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.CenterStart
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.Center
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.CenterEnd
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomStart
        assertEquals(expected = ScaleDecider(Scale.START_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomCenter
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomEnd
        assertEquals(expected = ScaleDecider(Scale.END_CROP), actual = target.getScaleDecider())

        state.contentScale = ContentScale.Inside
        state.alignment = Alignment.TopStart
        assertEquals(expected = ScaleDecider(Scale.START_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.TopCenter
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.TopEnd
        assertEquals(expected = ScaleDecider(Scale.END_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.CenterStart
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.Center
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.CenterEnd
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomStart
        assertEquals(expected = ScaleDecider(Scale.START_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomCenter
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())
        state.alignment = Alignment.BottomEnd
        assertEquals(expected = ScaleDecider(Scale.END_CROP), actual = target.getScaleDecider())

        state.contentScale = ContentScale.FillWidth
        assertEquals(expected = ScaleDecider(Scale.FILL), actual = target.getScaleDecider())

        state.contentScale = ContentScale.FillHeight
        assertEquals(expected = ScaleDecider(Scale.FILL), actual = target.getScaleDecider())

        state.contentScale = ContentScale.FillBounds
        assertEquals(expected = ScaleDecider(Scale.FILL), actual = target.getScaleDecider())
    }

    @Test
    fun testImageOptions() {
        val context = getTestContext()

        assertEquals(
            expected = null,
            actual = AsyncImageTarget(
                imageState = AsyncImageState(
                    context = context,
                    inspectionMode = false,
                    lifecycle = GlobalLifecycle,
                    imageOptions = null
                )
            ).getImageOptions()
        )

        assertEquals(
            expected = ImageOptions(),
            actual = AsyncImageTarget(
                imageState = AsyncImageState(
                    context = context,
                    inspectionMode = false,
                    lifecycle = GlobalLifecycle,
                    imageOptions = ImageOptions()
                )
            ).getImageOptions()
        )
    }

    @Test
    fun testOnStartOnSuccessOnError() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)

        assertEquals(expected = null, actual = target.painter)
        assertEquals(expected = null, actual = state.painter)
        assertEquals(expected = null, actual = state.painterState)

        val painter1 = ColorPainter(Color.Red)
        val painter2 = ColorPainter(Color.Green)
        val painter3 = ColorPainter(Color.Yellow)

        target.onStart(sketch, request, painter1.asImage())
        assertEquals(expected = painter1, actual = target.painter)
        assertEquals(expected = painter1, actual = state.painter)
        assertEquals(
            expected = PainterState.Loading(painter1),
            actual = state.painterState
        )

        target.onSuccess(sketch, request, fakeSuccessImageResult(context), painter2.asImage())
        assertEquals(expected = painter2, actual = target.painter)
        assertEquals(expected = painter2, actual = state.painter)
        assertEquals(
            expected = PainterState.Success(fakeSuccessImageResult(context), painter2),
            actual = state.painterState
        )

        target.onError(sketch, request, fakeErrorImageResult(context), painter3.asImage())
        assertEquals(expected = painter3, actual = target.painter)
        assertEquals(expected = painter3, actual = state.painter)
        assertEquals(
            expected = PainterState.Error(fakeErrorImageResult(context), painter3),
            actual = state.painterState
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val state1 = AsyncImageState(context, false, GlobalLifecycle, null)
        val state2 = AsyncImageState(context, false, GlobalLifecycle, null)
        val element1 = AsyncImageTarget(
            state1,
        )
        val element11 = AsyncImageTarget(
            state1,
        )
        val element2 = AsyncImageTarget(
            state2,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        val target = AsyncImageTarget(state)
        assertEquals(
            expected = "AsyncImageTarget($state)",
            actual = target.toString()
        )
    }
}