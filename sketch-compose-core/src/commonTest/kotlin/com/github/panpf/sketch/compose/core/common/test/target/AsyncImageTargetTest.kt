package com.github.panpf.sketch.compose.core.common.test.target

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.painter.ImageBitmapPainter
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
import com.github.panpf.sketch.util.screenSize
import com.github.panpf.sketch.util.toIntSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class AsyncImageTargetTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        )
    }

    @Test
    fun testWindowContainerSize() {
        val context = getTestContext()
        AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        ).apply {
            assertEquals(expected = context.screenSize().toIntSize(), actual = windowContainerSize)
        }

        assertFailsWith(IllegalArgumentException::class) {
            AsyncImageTarget(
                context = context,
                lifecycle = TestLifecycle(),
                imageOptions = ImageOptions(),
            ).apply {
                windowContainerSize = IntSize(0, 1000)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            AsyncImageTarget(
                context = context,
                lifecycle = TestLifecycle(),
                imageOptions = ImageOptions(),
            ).apply {
                windowContainerSize = IntSize(-1, 1000)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            AsyncImageTarget(
                context = context,
                lifecycle = TestLifecycle(),
                imageOptions = ImageOptions(),
            ).apply {
                windowContainerSize = IntSize(1000, 0)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            AsyncImageTarget(
                context = context,
                lifecycle = TestLifecycle(),
                imageOptions = ImageOptions(),
            ).apply {
                windowContainerSize = IntSize(1000, -1)
            }
        }
        AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        ).apply {
            windowContainerSize = IntSize(3000, 2000)
        }.apply {
            assertEquals(expected = IntSize(3000, 2000), actual = windowContainerSize)
        }
    }

    @Test
    fun testPainterAndRemembered() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val target = AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        )
        assertEquals(expected = null, actual = target.painterState.value)
        assertEquals(expected = null, actual = target.painter)

        val painter1 = ColorPainter(Color.Red)
        val painter2 = RememberedPainter(ColorPainter(Color.Green))
        val painter3 = ColorPainter(Color.Yellow)
        val painter4 = RememberedPainter(ColorPainter(Color.Blue))

        target.onSuccess(sketch, request, fakeSuccessImageResult(context), painter1.asImage())
        assertSame(expected = painter1, actual = target.painterState.value)
        assertSame(expected = painter1, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 0, actual = painter4.rememberedCounter.count)

        target.onSuccess(sketch, request, fakeSuccessImageResult(context), painter2.asImage())
        assertSame(expected = painter2, actual = target.painterState.value)
        assertSame(expected = painter2, actual = target.painter)
        assertEquals(expected = 1, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 0, actual = painter4.rememberedCounter.count)

        target.onSuccess(sketch, request, fakeSuccessImageResult(context), painter3.asImage())
        assertSame(expected = painter3, actual = target.painterState.value)
        assertSame(expected = painter3, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 0, actual = painter4.rememberedCounter.count)

        target.onSuccess(sketch, request, fakeSuccessImageResult(context), painter4.asImage())
        assertSame(expected = painter4, actual = target.painterState.value)
        assertSame(expected = painter4, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 1, actual = painter4.rememberedCounter.count)

        target.onForgotten()
        assertSame(expected = painter4, actual = target.painterState.value)
        assertSame(expected = painter4, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 0, actual = painter4.rememberedCounter.count)

        target.onRemembered()
        assertSame(expected = painter4, actual = target.painterState.value)
        assertSame(expected = painter4, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 1, actual = painter4.rememberedCounter.count)

        target.onForgotten()
        assertSame(expected = painter4, actual = target.painterState.value)
        assertSame(expected = painter4, actual = target.painter)
        assertEquals(expected = 0, actual = painter2.rememberedCounter.count)
        assertEquals(expected = 0, actual = painter4.rememberedCounter.count)
    }

    @Test
    fun testPreviewImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val target = AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        )
        assertEquals(expected = null, actual = target.painterState.value)
        assertEquals(expected = null, actual = target.painter)
        assertEquals(expected = null, actual = target.painterStateState.value)
        assertEquals(expected = null, actual = target.loadStateState.value)

        val previewPainter = ColorPainter(Color.Red)

        target.setPreviewImage(sketch, request, previewPainter.asImage())
        assertEquals(expected = previewPainter, actual = target.painterState.value)
        assertEquals(expected = previewPainter, actual = target.painter)
        assertEquals(
            expected = PainterState.Loading(previewPainter),
            actual = target.painterStateState.value
        )
        assertEquals(
            expected = LoadState.Started(request),
            actual = target.loadStateState.value
        )
    }

    @Test
    fun testContentScale() {
        val context = getTestContext()
        val target = AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        )
        assertEquals(expected = null, actual = target.contentScaleMutableState.value)
        assertEquals(expected = true, actual = target.fitScale)

        target.contentScaleMutableState.value = ContentScale.Inside
        assertEquals(expected = ContentScale.Inside, actual = target.contentScaleMutableState.value)
        assertEquals(expected = true, actual = target.fitScale)

        target.contentScaleMutableState.value = ContentScale.Fit
        assertEquals(expected = ContentScale.Fit, actual = target.contentScaleMutableState.value)
        assertEquals(expected = true, actual = target.fitScale)

        target.contentScaleMutableState.value = ContentScale.Crop
        assertEquals(expected = ContentScale.Crop, actual = target.contentScaleMutableState.value)
        assertEquals(expected = false, actual = target.fitScale)

        target.contentScaleMutableState.value = ContentScale.None
        assertEquals(expected = ContentScale.None, actual = target.contentScaleMutableState.value)
        assertEquals(expected = false, actual = target.fitScale)

        target.contentScaleMutableState.value = ContentScale.FillWidth
        assertEquals(
            expected = ContentScale.FillWidth,
            actual = target.contentScaleMutableState.value
        )
        assertEquals(expected = false, actual = target.fitScale)

        target.contentScaleMutableState.value = ContentScale.FillHeight
        assertEquals(
            expected = ContentScale.FillHeight,
            actual = target.contentScaleMutableState.value
        )
        assertEquals(expected = false, actual = target.fitScale)

        target.contentScaleMutableState.value = ContentScale.FillBounds
        assertEquals(
            expected = ContentScale.FillBounds,
            actual = target.contentScaleMutableState.value
        )
        assertEquals(expected = false, actual = target.fitScale)
    }

    @Test
    fun testFilterQuality() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val target = AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        )
        assertEquals(expected = FilterQuality.Low, actual = target.filterQuality)
        assertEquals(expected = null, actual = target.filterQualityMutableState.value)
        target.onSuccess(
            sketch,
            request,
            fakeSuccessImageResult(context),
            createBitmapImage(101, 202)
        ).apply {
            assertEquals(
                expected = FilterQuality.Low,
                actual = target.painter!!.asOrThrow<ImageBitmapPainter>().filterQuality
            )
        }

        target.filterQualityMutableState.value = FilterQuality.High
        assertEquals(expected = FilterQuality.High, actual = target.filterQuality)
        assertEquals(expected = FilterQuality.High, actual = target.filterQualityMutableState.value)
        target.onSuccess(
            sketch,
            request,
            fakeSuccessImageResult(context),
            createBitmapImage(101, 202)
        ).apply {
            assertEquals(
                expected = FilterQuality.High,
                actual = target.painter!!.asOrThrow<ImageBitmapPainter>().filterQuality
            )
        }
    }

    @Test
    fun testSize() {
        val context = getTestContext()
        val windowContainerSize = context.screenSize()
        val target = AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        )
        assertEquals(expected = null, actual = target.sizeState.value)
        assertEquals(expected = null, actual = target.getSizeResolver().sizeState.value)

        target.setSize(IntSize(0, 1000))
        assertEquals(
            expected = IntSize(windowContainerSize.width, 1000),
            actual = target.sizeState.value
        )
        assertEquals(
            expected = IntSize(windowContainerSize.width, 1000),
            actual = target.getSizeResolver().sizeState.value
        )

        target.setSize(IntSize(1000, 0))
        assertEquals(
            expected = IntSize(1000, windowContainerSize.height),
            actual = target.sizeState.value
        )
        assertEquals(
            expected = IntSize(1000, windowContainerSize.height),
            actual = target.getSizeResolver().sizeState.value
        )

        target.setSize(IntSize(0, 0))
        assertEquals(
            expected = IntSize(windowContainerSize.width, windowContainerSize.height),
            actual = target.sizeState.value
        )
        assertEquals(
            expected = IntSize(windowContainerSize.width, windowContainerSize.height),
            actual = target.getSizeResolver().sizeState.value
        )

        target.setSize(IntSize(300, 400))
        assertEquals(expected = IntSize(300, 400), actual = target.sizeState.value)
        assertEquals(
            expected = IntSize(300, 400),
            actual = target.getSizeResolver().sizeState.value
        )
    }

    @Test
    fun testRequestManagerAndRemembered() {
        val context = getTestContext()
        val target = AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        )
        assertSame(expected = target.getRequestManager(), actual = target.getRequestManager())
        assertEquals(expected = 0, actual = target.getRequestManager().rememberedCounter.count)

        target.onRemembered()
        assertEquals(expected = 1, actual = target.getRequestManager().rememberedCounter.count)

        target.onRemembered()
        assertEquals(expected = 2, actual = target.getRequestManager().rememberedCounter.count)

        target.onForgotten()
        assertEquals(expected = 1, actual = target.getRequestManager().rememberedCounter.count)

        target.onForgotten()
        assertEquals(expected = 0, actual = target.getRequestManager().rememberedCounter.count)

        target.onForgotten()
        assertEquals(expected = 0, actual = target.getRequestManager().rememberedCounter.count)
    }

    @Test
    fun testListenerAndProgressListener() {
        val context = getTestContext()
        val target = AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        )
        assertSame(expected = target.getListener(), actual = target.getListener())
        assertSame(expected = target.getProgressListener(), actual = target.getProgressListener())
    }

    @Test
    fun testLifecycleResolver() {
        val context = getTestContext()
        val lifecycle = TestLifecycle()
        val target = AsyncImageTarget(
            context = context,
            lifecycle = lifecycle,
            imageOptions = ImageOptions(),
        )
        assertEquals(
            expected = LifecycleResolver(lifecycle),
            actual = target.getLifecycleResolver()
        )
    }

    @Test
    fun testSizeResolver() {
        val context = getTestContext()
        val lifecycle = TestLifecycle()
        val target = AsyncImageTarget(
            context = context,
            lifecycle = lifecycle,
            imageOptions = ImageOptions(),
        )
        assertSame(
            expected = target.getSizeResolver(),
            actual = target.getSizeResolver()
        )
    }

    @Test
    fun testScaleDecider() {
        val context = getTestContext()
        val lifecycle = TestLifecycle()
        val target = AsyncImageTarget(
            context = context,
            lifecycle = lifecycle,
            imageOptions = ImageOptions(),
        )
        assertEquals(expected = null, actual = target.getScaleDecider())

        target.contentScaleMutableState.value = ContentScale.Fit
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())

        target.contentScaleMutableState.value = ContentScale.Crop
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())

        target.contentScaleMutableState.value = ContentScale.None
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())

        target.contentScaleMutableState.value = ContentScale.Inside
        assertEquals(expected = ScaleDecider(Scale.CENTER_CROP), actual = target.getScaleDecider())

        target.contentScaleMutableState.value = ContentScale.FillWidth
        assertEquals(expected = ScaleDecider(Scale.FILL), actual = target.getScaleDecider())

        target.contentScaleMutableState.value = ContentScale.FillHeight
        assertEquals(expected = ScaleDecider(Scale.FILL), actual = target.getScaleDecider())

        target.contentScaleMutableState.value = ContentScale.FillBounds
        assertEquals(expected = ScaleDecider(Scale.FILL), actual = target.getScaleDecider())
    }

    @Test
    fun testImageOptions() {
        val context = getTestContext()
        val target1 = AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = null,
        )
        assertEquals(
            expected = null,
            actual = target1.getImageOptions()
        )

        val target2 = AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        )
        assertEquals(
            expected = ImageOptions(),
            actual = target2.getImageOptions()
        )
    }

    @Test
    fun testOnStartOnSuccessOnError() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val target = AsyncImageTarget(
            context = context,
            lifecycle = TestLifecycle(),
            imageOptions = ImageOptions(),
        )

        assertEquals(expected = null, actual = target.painter)
        assertEquals(expected = null, actual = target.painterState.value)
        assertEquals(expected = null, actual = target.painterStateState.value)

        val painter1 = ColorPainter(Color.Red)
        val painter2 = ColorPainter(Color.Green)
        val painter3 = ColorPainter(Color.Yellow)

        target.onStart(sketch, request, painter1.asImage())
        assertEquals(expected = painter1, actual = target.painter)
        assertEquals(expected = painter1, actual = target.painterState.value)
        assertEquals(
            expected = PainterState.Loading(painter1),
            actual = target.painterStateState.value
        )

        target.onSuccess(sketch, request, fakeSuccessImageResult(context), painter2.asImage())
        assertEquals(expected = painter2, actual = target.painter)
        assertEquals(expected = painter2, actual = target.painterState.value)
        assertEquals(
            expected = PainterState.Success(fakeSuccessImageResult(context), painter2),
            actual = target.painterStateState.value
        )

        target.onError(sketch, request, fakeErrorImageResult(context), painter3.asImage())
        assertEquals(expected = painter3, actual = target.painter)
        assertEquals(expected = painter3, actual = target.painterState.value)
        assertEquals(
            expected = PainterState.Error(fakeErrorImageResult(context), painter3),
            actual = target.painterStateState.value
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val lifecycle1 = TestLifecycle()
        val lifecycle2 = TestLifecycle()
        val element1 = AsyncImageTarget(
            context = context,
            lifecycle = lifecycle1,
            imageOptions = ImageOptions(),
        )
        val element11 = AsyncImageTarget(
            context = context,
            lifecycle = lifecycle1,
            imageOptions = ImageOptions(),
        )
        val element2 = AsyncImageTarget(
            context = context,
            lifecycle = lifecycle2,
            imageOptions = ImageOptions(),
        )
        val element3 = AsyncImageTarget(
            context = context,
            lifecycle = lifecycle1,
            imageOptions = ImageOptions { size(101, 202) },
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val lifecycle = TestLifecycle()
        val options = ImageOptions()
        val target = AsyncImageTarget(
            context = context,
            lifecycle = lifecycle,
            imageOptions = options,
        )
        assertEquals(
            expected = "AsyncImageTarget(context=$context, lifecycle=$lifecycle, options=$options)",
            actual = target.toString()
        )
    }
}