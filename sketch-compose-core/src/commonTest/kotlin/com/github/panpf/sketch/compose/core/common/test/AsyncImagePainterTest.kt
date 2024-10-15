package com.github.panpf.sketch.compose.core.common.test

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.AsyncImagePainter
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.rememberAsyncImagePainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.LifecycleContainer
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.TestLifecycle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

@OptIn(ExperimentalTestApi::class)
class AsyncImagePainterTest {

    @Test
    fun testRememberAsyncImagePainter() {
        val (_, sketch) = getTestContextAndSketch()
        runComposeUiTest {
            var painter1: AsyncImagePainter? = null
            var painter2: AsyncImagePainter? = null
            var painter3: AsyncImagePainter? = null
            var imageState2: AsyncImageState? = null
            var imageState3: AsyncImageState? = null
            setContent {
                LifecycleContainer {
                    painter1 = rememberAsyncImagePainter("https://www.test.com/test.jpg", sketch)

                    painter2 = rememberAsyncImagePainter(
                        "https://www.test.com/test.jpg",
                        sketch,
                        rememberAsyncImageState().apply { imageState2 = this },
                        ContentScale.Crop,
                        FilterQuality.High
                    )

                    painter3 = rememberAsyncImagePainter(
                        uri = "https://www.test.com/test.jpg",
                        sketch = sketch,
                        state = rememberAsyncImageState().apply { imageState3 = this },
                        contentScale = ContentScale.Inside,
                        filterQuality = FilterQuality.Medium
                    )
                }
            }
            waitForIdle()

            painter1!!.apply {
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Fit, actual = state.contentScale)
                assertEquals(expected = DefaultFilterQuality, actual = state.filterQuality)
            }
            painter2!!.apply {
                assertSame(expected = imageState2, actual = state)
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Crop, actual = state.contentScale)
                assertEquals(expected = FilterQuality.High, actual = state.filterQuality)
            }
            painter3!!.apply {
                assertSame(expected = imageState3, actual = state)
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Inside, actual = state.contentScale)
                assertEquals(expected = FilterQuality.Medium, actual = state.filterQuality)
            }
        }
    }

    @Test
    fun testRememberAsyncImagePainter2() {
        val (_, sketch) = getTestContextAndSketch()
        runComposeUiTest {
            var painter1: AsyncImagePainter? = null
            var painter2: AsyncImagePainter? = null
            var painter3: AsyncImagePainter? = null
            var imageState2: AsyncImageState? = null
            var imageState3: AsyncImageState? = null
            setContent {
                LifecycleContainer {
                    painter1 =
                        rememberAsyncImagePainter(
                            ComposableImageRequest("https://www.test.com/test.jpg"),
                            sketch
                        )

                    painter2 = rememberAsyncImagePainter(
                        ComposableImageRequest("https://www.test.com/test.jpg"),
                        sketch,
                        rememberAsyncImageState().apply { imageState2 = this },
                        ContentScale.Crop,
                        FilterQuality.High
                    )

                    painter3 = rememberAsyncImagePainter(
                        request = ComposableImageRequest("https://www.test.com/test.jpg"),
                        sketch = sketch,
                        state = rememberAsyncImageState().apply { imageState3 = this },
                        contentScale = ContentScale.Inside,
                        filterQuality = FilterQuality.Medium
                    )
                }
            }
            waitForIdle()

            painter1!!.apply {
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Fit, actual = state.contentScale)
                assertEquals(expected = DefaultFilterQuality, actual = state.filterQuality)
            }
            painter2!!.apply {
                assertSame(expected = imageState2, actual = state)
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Crop, actual = state.contentScale)
                assertEquals(expected = FilterQuality.High, actual = state.filterQuality)
            }
            painter3!!.apply {
                assertSame(expected = imageState3, actual = state)
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Inside, actual = state.contentScale)
                assertEquals(expected = FilterQuality.Medium, actual = state.filterQuality)
            }
        }
    }

    @Test
    fun testIntrinsicSize() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val asyncImageState = AsyncImageState(
            inspectionMode = false,
            lifecycle = TestLifecycle(),
            windowContainerSize = IntSize(1024, 768),
            imageOptions = null
        )
        val asyncImagePainter = AsyncImagePainter(asyncImageState)

        assertEquals(null, asyncImageState.target.painter)
        assertEquals(Size.Unspecified, asyncImagePainter.intrinsicSize)

        asyncImageState.target.onSuccess(
            sketch = sketch,
            request = request,
            result = SizeColorPainter(Color.Red, Size(101f, 202f)).asImage()
        )
        assertEquals(SizeColorPainter(Color.Red, Size(101f, 202f)), asyncImageState.target.painter)
        assertEquals(Size(101f, 202f), asyncImagePainter.intrinsicSize)
    }

    @Test
    fun testOnDraw() {
        // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testEqualsAndHashCode() {
        val asyncImageState1 = AsyncImageState(
            inspectionMode = false,
            lifecycle = TestLifecycle(),
            windowContainerSize = IntSize(1024, 768),
            imageOptions = null
        )
        val asyncImageState2 = AsyncImageState(
            inspectionMode = false,
            lifecycle = TestLifecycle(),
            windowContainerSize = IntSize(1024, 768),
            imageOptions = null
        )
        val element1 = AsyncImagePainter(asyncImageState1)
        val element11 = AsyncImagePainter(asyncImageState1)
        val element2 = AsyncImagePainter(asyncImageState2)

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
    }

    @Test
    fun testToString() {
        val asyncImageState = AsyncImageState(
            inspectionMode = false,
            lifecycle = TestLifecycle(),
            windowContainerSize = IntSize(1024, 768),
            imageOptions = null
        )
        val element = AsyncImagePainter(asyncImageState)
        assertEquals(
            expected = "AsyncImagePainter(state=${asyncImageState})",
            actual = element.toString()
        )
    }
}