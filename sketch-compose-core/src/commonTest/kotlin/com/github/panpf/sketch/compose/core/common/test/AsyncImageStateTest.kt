@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.painter.ComposeBitmapPainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.windowContainerSize
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AsyncImageStateTest {

    @Test
    fun testRememberAsyncImageState() {
        val testLifecycle = TestLifecycle()
        runComposeUiTest {
            setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides testLifecycle.owner) {
                    rememberAsyncImageState().apply {
                        assertEquals(expected = testLifecycle, actual = lifecycle)
                        assertEquals(expected = false, actual = inspectionMode)
                        assertEquals(expected = windowContainerSize(), actual = windowContainerSize)
                        assertEquals(expected = null, actual = imageOptions)
                    }

                    CompositionLocalProvider(LocalInspectionMode provides true) {
                        rememberAsyncImageState().apply {
                            assertEquals(expected = GlobalLifecycle, actual = lifecycle)
                            assertEquals(expected = true, actual = inspectionMode)
                            assertEquals(
                                expected = windowContainerSize(),
                                actual = windowContainerSize
                            )
                            assertEquals(expected = null, actual = imageOptions)
                        }
                    }

                    rememberAsyncImageState(ImageOptions { size(101, 202) }).apply {
                        assertEquals(expected = testLifecycle, actual = lifecycle)
                        assertEquals(expected = false, actual = inspectionMode)
                        assertEquals(expected = windowContainerSize(), actual = windowContainerSize)
                        assertEquals(
                            expected = ImageOptions { size(101, 202) },
                            actual = imageOptions
                        )
                    }

                    rememberAsyncImageState {
                        ImageOptions { size(202, 101) }
                    }.apply {
                        assertEquals(expected = testLifecycle, actual = lifecycle)
                        assertEquals(expected = false, actual = inspectionMode)
                        assertEquals(expected = windowContainerSize(), actual = windowContainerSize)
                        assertEquals(
                            expected = ImageOptions { size(202, 101) },
                            actual = imageOptions
                        )
                    }
                }
            }
        }
    }

    @Test
    fun testConstructor() {
        AsyncImageState(true, TestLifecycle(), IntSize(1080, 720), ImageOptions())

        AsyncImageState(
            inspectionMode = true,
            lifecycle = TestLifecycle(),
            windowContainerSize = IntSize(1080, 720),
            imageOptions = ImageOptions()
        )
    }

    @Test
    fun testLifecycle() {
        val lifecycle = TestLifecycle()
        val asyncImageState = AsyncImageState(
            inspectionMode = true,
            lifecycle = lifecycle,
            windowContainerSize = IntSize(1080, 720),
            imageOptions = ImageOptions()
        )
        assertEquals(
            expected = LifecycleResolver(lifecycle),
            actual = asyncImageState.target.getLifecycleResolver()
        )
    }

    @Test
    fun testImageOptions() {
        val asyncImageState1 = AsyncImageState(
            inspectionMode = true,
            lifecycle = TestLifecycle(),
            windowContainerSize = IntSize(1080, 720),
            imageOptions = null
        )
        assertEquals(
            expected = null,
            actual = asyncImageState1.target.getImageOptions()
        )

        val asyncImageState2 = AsyncImageState(
            inspectionMode = true,
            lifecycle = TestLifecycle(),
            windowContainerSize = IntSize(1080, 720),
            imageOptions = ImageOptions()
        )
        assertEquals(
            expected = ImageOptions(),
            actual = asyncImageState2.target.getImageOptions()
        )
    }

    @Test
    fun testContentScale() {
        val asyncImageState = AsyncImageState(
            inspectionMode = true,
            lifecycle = TestLifecycle(),
            windowContainerSize = IntSize(1080, 720),
            imageOptions = ImageOptions()
        )
        assertEquals(
            expected = null,
            actual = asyncImageState.contentScale
        )
        assertEquals(
            expected = true,
            actual = asyncImageState.target.fitScale
        )
        assertEquals(
            expected = null,
            actual = asyncImageState.target.getScaleDecider()
        )

        asyncImageState.contentScale = ContentScale.Fit
        assertEquals(
            expected = ContentScale.Fit,
            actual = asyncImageState.contentScale
        )
        assertEquals(
            expected = true,
            actual = asyncImageState.target.fitScale
        )
        assertEquals(
            expected = ScaleDecider(Scale.CENTER_CROP),
            actual = asyncImageState.target.getScaleDecider()
        )

        asyncImageState.contentScale = ContentScale.Inside
        assertEquals(
            expected = ContentScale.Inside,
            actual = asyncImageState.contentScale
        )
        assertEquals(
            expected = true,
            actual = asyncImageState.target.fitScale
        )
        assertEquals(
            expected = ScaleDecider(Scale.CENTER_CROP),
            actual = asyncImageState.target.getScaleDecider()
        )

        asyncImageState.contentScale = ContentScale.Inside
        assertEquals(
            expected = ContentScale.Inside,
            actual = asyncImageState.contentScale
        )
        assertEquals(
            expected = true,
            actual = asyncImageState.target.fitScale
        )
        assertEquals(
            expected = ScaleDecider(Scale.CENTER_CROP),
            actual = asyncImageState.target.getScaleDecider()
        )

        asyncImageState.contentScale = ContentScale.Crop
        assertEquals(
            expected = ContentScale.Crop,
            actual = asyncImageState.contentScale
        )
        assertEquals(
            expected = false,
            actual = asyncImageState.target.fitScale
        )
        assertEquals(
            expected = ScaleDecider(Scale.CENTER_CROP),
            actual = asyncImageState.target.getScaleDecider()
        )

        asyncImageState.contentScale = ContentScale.None
        assertEquals(
            expected = ContentScale.None,
            actual = asyncImageState.contentScale
        )
        assertEquals(
            expected = false,
            actual = asyncImageState.target.fitScale
        )
        assertEquals(
            expected = ScaleDecider(Scale.CENTER_CROP),
            actual = asyncImageState.target.getScaleDecider()
        )

        asyncImageState.contentScale = ContentScale.FillWidth
        assertEquals(
            expected = ContentScale.FillWidth,
            actual = asyncImageState.contentScale
        )
        assertEquals(
            expected = false,
            actual = asyncImageState.target.fitScale
        )
        assertEquals(
            expected = ScaleDecider(Scale.FILL),
            actual = asyncImageState.target.getScaleDecider()
        )

        asyncImageState.contentScale = ContentScale.FillHeight
        assertEquals(
            expected = ContentScale.FillHeight,
            actual = asyncImageState.contentScale
        )
        assertEquals(
            expected = false,
            actual = asyncImageState.target.fitScale
        )
        assertEquals(
            expected = ScaleDecider(Scale.FILL),
            actual = asyncImageState.target.getScaleDecider()
        )

        asyncImageState.contentScale = ContentScale.FillBounds
        assertEquals(
            expected = ContentScale.FillBounds,
            actual = asyncImageState.contentScale
        )
        assertEquals(
            expected = false,
            actual = asyncImageState.target.fitScale
        )
        assertEquals(
            expected = ScaleDecider(Scale.FILL),
            actual = asyncImageState.target.getScaleDecider()
        )
    }

    @Test
    fun testFilterQuality() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val asyncImageState = AsyncImageState(
            inspectionMode = true,
            lifecycle = TestLifecycle(),
            windowContainerSize = IntSize(1080, 720),
            imageOptions = ImageOptions()
        )
        val target = asyncImageState.target
        assertEquals(expected = null, actual = asyncImageState.filterQuality)
        assertEquals(expected = FilterQuality.Low, actual = target.filterQuality)
        assertEquals(expected = null, actual = target.filterQualityMutableState.value)
        target.onSuccess(sketch, request, createBitmapImage(101, 202)).apply {
            assertEquals(
                expected = FilterQuality.Low,
                actual = asyncImageState.painter!!.asOrThrow<ComposeBitmapPainter>().filterQuality
            )
        }

        asyncImageState.filterQuality = FilterQuality.High
        assertEquals(expected = FilterQuality.High, actual = asyncImageState.filterQuality)
        assertEquals(expected = FilterQuality.High, actual = target.filterQuality)
        assertEquals(expected = FilterQuality.High, actual = target.filterQualityMutableState.value)
        target.onSuccess(sketch, request, createBitmapImage(101, 202)).apply {
            assertEquals(
                expected = FilterQuality.High,
                actual = asyncImageState.painter!!.asOrThrow<ComposeBitmapPainter>().filterQuality
            )
        }
    }

    @Test
    fun testSize() {
        val windowContainerSize = IntSize(1080, 720)
        val asyncImageState = AsyncImageState(
            inspectionMode = true,
            lifecycle = TestLifecycle(),
            windowContainerSize = IntSize(1080, 720),
            imageOptions = ImageOptions()
        )
        assertEquals(expected = null, actual = asyncImageState.size)
        assertEquals(expected = null, actual = asyncImageState.target.sizeState.value)
        assertEquals(
            expected = null,
            actual = asyncImageState.target.getSizeResolver().sizeState.value
        )

        asyncImageState.setSize(IntSize(300, 400))
        assertEquals(expected = IntSize(300, 400), actual = asyncImageState.size)
        assertEquals(expected = IntSize(300, 400), actual = asyncImageState.target.sizeState.value)
        assertEquals(
            expected = IntSize(300, 400),
            actual = asyncImageState.target.getSizeResolver().sizeState.value
        )

        asyncImageState.setSize(IntSize(0, 1000))
        assertEquals(
            expected = IntSize(windowContainerSize.width, 1000),
            actual = asyncImageState.size
        )
        assertEquals(
            expected = IntSize(windowContainerSize.width, 1000),
            actual = asyncImageState.target.sizeState.value
        )
        assertEquals(
            expected = IntSize(windowContainerSize.width, 1000),
            actual = asyncImageState.target.getSizeResolver().sizeState.value
        )

        asyncImageState.setSize(IntSize(1000, 0))
        assertEquals(
            expected = IntSize(1000, windowContainerSize.height),
            actual = asyncImageState.size
        )
        assertEquals(
            expected = IntSize(1000, windowContainerSize.height),
            actual = asyncImageState.target.sizeState.value
        )
        assertEquals(
            expected = IntSize(1000, windowContainerSize.height),
            actual = asyncImageState.target.getSizeResolver().sizeState.value
        )

        asyncImageState.setSize(IntSize(0, 0))
        assertEquals(
            expected = IntSize(windowContainerSize.width, windowContainerSize.height),
            actual = asyncImageState.size
        )
        assertEquals(
            expected = IntSize(windowContainerSize.width, windowContainerSize.height),
            actual = asyncImageState.target.sizeState.value
        )
        assertEquals(
            expected = windowContainerSize,
            actual = asyncImageState.target.getSizeResolver().sizeState.value
        )
    }

    @Test
    fun testRemembered() {
        // TODO test
    }

    @Test
    fun testPreview() {
        // TODO test
    }

    @Test
    fun testLoad() {
        // TODO test
    }

    @Test
    fun testRestart() {
        // TODO test
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val lifecycle = TestLifecycle()
        val windowContainerSize = IntSize(1080, 720)
        val imageOptions = ImageOptions()
        val element1 =
            AsyncImageState(true, lifecycle, windowContainerSize, imageOptions)
        val element11 =
            AsyncImageState(true, lifecycle, windowContainerSize, imageOptions)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val asyncImageState =
            AsyncImageState(true, TestLifecycle(), IntSize(1080, 720), ImageOptions())
        assertEquals(
            expected = "AsyncImageState@${asyncImageState.toHexString()}",
            actual = asyncImageState.toString()
        )
    }
}