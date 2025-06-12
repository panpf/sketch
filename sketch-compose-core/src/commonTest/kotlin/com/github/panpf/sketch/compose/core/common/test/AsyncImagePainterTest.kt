package com.github.panpf.sketch.compose.core.common.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.rememberAsyncImagePainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.LifecycleContainer
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.fakeSuccessImageResult
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.toIntSize
import com.github.panpf.sketch.util.windowContainerSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

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
                    painter1 = rememberAsyncImagePainter(
                        uri = "https://www.test.com/test.jpg",
                        sketch = sketch
                    )

                    painter2 = rememberAsyncImagePainter(
                        uri = "https://www.test.com/test.jpg",
                        sketch = sketch,
                        state = rememberAsyncImageState().apply { imageState2 = this },
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.High
                    )

                    painter3 = rememberAsyncImagePainter(
                        uri = "https://www.test.com/test.jpg",
                        sketch = sketch,
                        state = rememberAsyncImageState().apply { imageState3 = this },
                        contentScale = ContentScale.Inside,
                        alignment = Alignment.BottomEnd,
                        filterQuality = FilterQuality.Medium
                    )
                }
            }
            waitForIdle()

            painter1!!.apply {
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Fit, actual = state.contentScale)
                assertEquals(expected = Alignment.Center, actual = state.alignment)
                assertEquals(expected = DefaultFilterQuality, actual = state.filterQuality)
            }
            painter2!!.apply {
                assertSame(expected = imageState2, actual = state)
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Crop, actual = state.contentScale)
                assertEquals(expected = Alignment.Center, actual = state.alignment)
                assertEquals(expected = FilterQuality.High, actual = state.filterQuality)
            }
            painter3!!.apply {
                assertSame(expected = imageState3, actual = state)
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Inside, actual = state.contentScale)
                assertEquals(expected = Alignment.BottomEnd, actual = state.alignment)
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
                            request = ComposableImageRequest(uri = "https://www.test.com/test.jpg"),
                            sketch = sketch
                        )

                    painter2 = rememberAsyncImagePainter(
                        request = ComposableImageRequest(uri = "https://www.test.com/test.jpg"),
                        sketch = sketch,
                        state = rememberAsyncImageState().apply { imageState2 = this },
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.High
                    )

                    painter3 = rememberAsyncImagePainter(
                        request = ComposableImageRequest("https://www.test.com/test.jpg"),
                        sketch = sketch,
                        state = rememberAsyncImageState().apply { imageState3 = this },
                        contentScale = ContentScale.Inside,
                        alignment = Alignment.BottomEnd,
                        filterQuality = FilterQuality.Medium
                    )
                }
            }
            waitForIdle()

            painter1!!.apply {
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Fit, actual = state.contentScale)
                assertEquals(expected = Alignment.Center, actual = state.alignment)
                assertEquals(expected = DefaultFilterQuality, actual = state.filterQuality)
            }
            painter2!!.apply {
                assertSame(expected = imageState2, actual = state)
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Crop, actual = state.contentScale)
                assertEquals(expected = Alignment.Center, actual = state.alignment)
                assertEquals(expected = FilterQuality.High, actual = state.filterQuality)
            }
            painter3!!.apply {
                assertSame(expected = imageState3, actual = state)
                assertSame(expected = sketch, actual = state.sketch)
                assertEquals(expected = ContentScale.Inside, actual = state.contentScale)
                assertEquals(expected = Alignment.BottomEnd, actual = state.alignment)
                assertEquals(expected = FilterQuality.Medium, actual = state.filterQuality)
            }
        }
    }

    @Test
    fun testIntrinsicSize() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = TestLifecycle(),
            imageOptions = null
        )
        val asyncImagePainter = AsyncImagePainter(asyncImageState)

        assertEquals(null, asyncImageState.target.painter)
        assertEquals(Size.Unspecified, asyncImagePainter.intrinsicSize)

        asyncImageState.target.onSuccess(
            sketch = sketch,
            request = request,
            result = fakeSuccessImageResult(context),
            image = SizeColorPainter(Color.Red, Size(101f, 202f)).asImage()
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
        val context = getTestContext()
        val asyncImageState1 = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = TestLifecycle(),
            imageOptions = null
        )
        val asyncImageState2 = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = TestLifecycle(),
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
        val context = getTestContext()
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = TestLifecycle(),
            imageOptions = null
        )
        val element = AsyncImagePainter(asyncImageState)
        assertEquals(
            expected = "AsyncImagePainter(state=${asyncImageState})",
            actual = element.toString()
        )
    }

    @Test
    fun testImageNoBounded() {
        val (_, sketch) = getTestContextAndSketch()

        // has bounded
        runComposeUiTest {
            var stateHolder: AsyncImageState? = null
            var windowContainerSizeHolder: IntSize? = null
            setContent {
                windowContainerSizeHolder = windowContainerSize()
                LifecycleContainer {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val state = rememberAsyncImageState().apply {
                            stateHolder = this
                        }
                        Image(
                            painter = rememberAsyncImagePainter(
                                uri = ResourceImages.jpeg.uri,
                                sketch = sketch,
                                state = state
                            ),
                            contentDescription = "test image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            waitUntil(timeoutMillis = 5000) {
                stateHolder?.result is ImageResult.Success
            }
            assertTrue(actual = stateHolder?.result is ImageResult.Success)
            assertEquals(
                expected = IntSize(
                    windowContainerSizeHolder!!.width,
                    windowContainerSizeHolder!!.height
                ),
                actual = (stateHolder?.result as ImageResult.Success).resize.size.toIntSize()
            )
        }

        /*
         * AsyncImagePainter requires the drawing size to load the image as the request size when drawing,
         * but the image here is 0 when width or height is 0, and it will not go to the drawing stage, so the request will never be executed.
         */
//        // width is not bounded
//        runComposeUiTest {
//            var stateHolder: AsyncImageState? = null
//            var windowContainerSizeHolder: IntSize? = null
//            setContent {
//                windowContainerSizeHolder = windowContainerSize()
//                LifecycleContainer {
//                    Box(
//                        modifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState())
//                    ) {
//                        val state = rememberAsyncImageState().apply {
//                            stateHolder = this
//                        }
//                        Image(
//                            painter = rememberAsyncImagePainter(
//                                uri = ResourceImages.jpeg.uri,
//                                sketch = sketch,
//                                state = state
//                            ),
//                            contentDescription = "test image",
//                            modifier = Modifier.fillMaxSize()
//                        )
//                    }
//                }
//            }
//            waitUntil(timeoutMillis = 5000) {
//                stateHolder?.result is ImageResult.Success
//            }
//            assertTrue(actual = stateHolder?.result is ImageResult.Success)
//            assertEquals(
//                expected = IntSize(0, windowContainerSizeHolder!!.height),
//                actual = (stateHolder?.result as ImageResult.Success).resize.size.toIntSize()
//            )
//        }
//
//        // height is not bounded
//        runComposeUiTest {
//            var stateHolder: AsyncImageState? = null
//            var windowContainerSizeHolder: IntSize? = null
//            setContent {
//                windowContainerSizeHolder = windowContainerSize()
//                LifecycleContainer {
//                    Box(
//                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
//                    ) {
//                        val state = rememberAsyncImageState().apply {
//                            stateHolder = this
//                        }
//                        Image(
//                            painter = rememberAsyncImagePainter(
//                                uri = ResourceImages.jpeg.uri,
//                                sketch = sketch,
//                                state = state
//                            ),
//                            contentDescription = "test image",
//                            modifier = Modifier.fillMaxSize()
//                        )
//                    }
//                }
//            }
//            waitUntil(timeoutMillis = 5000) {
//                stateHolder?.result is ImageResult.Success
//            }
//            assertTrue(actual = stateHolder?.result is ImageResult.Success)
//            assertEquals(
//                expected = IntSize(windowContainerSizeHolder!!.width, 0),
//                actual = (stateHolder?.result as ImageResult.Success).resize.size.toIntSize()
//            )
//        }
//
//        // width height is not bounded
//        runComposeUiTest {
//            var stateHolder: AsyncImageState? = null
//            var windowContainerSizeHolder: IntSize? = null
//            setContent {
//                windowContainerSizeHolder = windowContainerSize()
//                LifecycleContainer {
//                    Box(
//                        modifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState()).verticalScroll(
//                            rememberScrollState()
//                        )
//                    ) {
//                        val state = rememberAsyncImageState().apply {
//                            stateHolder = this
//                        }
//                        Image(
//                            painter = rememberAsyncImagePainter(
//                                uri = ResourceImages.jpeg.uri,
//                                sketch = sketch,
//                                state = state
//                            ),
//                            contentDescription = "test image",
//                            modifier = Modifier.fillMaxSize()
//                        )
//                    }
//                }
//            }
//            waitUntil(timeoutMillis = 5000) {
//                stateHolder?.result is ImageResult.Success
//            }
//            assertTrue(actual = stateHolder?.result is ImageResult.Success)
//            assertEquals(
//                expected = IntSize(0, 0),
//                actual = (stateHolder?.result as ImageResult.Success).resize.size.toIntSize()
//            )
//        }
    }

    @Test
    fun testImageNoBounded2() {
        val (_, sketch) = getTestContextAndSketch()

        // has bounded
        runComposeUiTest {
            var stateHolder: AsyncImageState? = null
            var windowContainerSizeHolder: IntSize? = null
            setContent {
                windowContainerSizeHolder = windowContainerSize()
                LifecycleContainer {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val state = rememberAsyncImageState().apply {
                            stateHolder = this
                        }
                        Image(
                            painter = rememberAsyncImagePainter(
                                uri = ResourceImages.jpeg.uri,
                                sketch = sketch,
                                state = state
                            ),
                            contentDescription = "test image",
                            modifier = Modifier.wrapContentSize()
                        )
                    }
                }
            }
            waitUntil(timeoutMillis = 5000) {
                stateHolder?.result is ImageResult.Success
            }
            assertTrue(actual = stateHolder?.result is ImageResult.Success)
            assertEquals(
                expected = IntSize(
                    windowContainerSizeHolder!!.width,
                    windowContainerSizeHolder!!.height
                ),
                actual = (stateHolder?.result as ImageResult.Success).resize.size.toIntSize()
            )
        }

        /*
         * AsyncImagePainter requires the drawing size to load the image as the request size when drawing,
         * but the image here is 0 when width or height is 0, and it will not go to the drawing stage, so the request will never be executed.
         */
//        // width is not bounded
//        runComposeUiTest {
//            var stateHolder: AsyncImageState? = null
//            var windowContainerSizeHolder: IntSize? = null
//            setContent {
//                windowContainerSizeHolder = windowContainerSize()
//                LifecycleContainer {
//                    Box(
//                        modifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState())
//                    ) {
//                        val state = rememberAsyncImageState().apply {
//                            stateHolder = this
//                        }
//                        Image(
//                            painter = rememberAsyncImagePainter(
//                                uri = ResourceImages.jpeg.uri,
//                                sketch = sketch,
//                                state = state
//                            ),
//                            contentDescription = "test image",
//                            modifier = Modifier.wrapContentSize()
//                        )
//                    }
//                }
//            }
//            waitUntil(timeoutMillis = 5000) {
//                stateHolder?.result is ImageResult.Success
//            }
//            assertTrue(actual = stateHolder?.result is ImageResult.Success)
//            assertEquals(
//                expected = IntSize(0, 0),
//                actual = (stateHolder?.result as ImageResult.Success).resize.size.toIntSize()
//            )
//        }
//
//        // height is not bounded
//        runComposeUiTest {
//            var stateHolder: AsyncImageState? = null
//            var windowContainerSizeHolder: IntSize? = null
//            setContent {
//                windowContainerSizeHolder = windowContainerSize()
//                LifecycleContainer {
//                    Box(
//                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
//                    ) {
//                        val state = rememberAsyncImageState().apply {
//                            stateHolder = this
//                        }
//                        Image(
//                            painter = rememberAsyncImagePainter(
//                                uri = ResourceImages.jpeg.uri,
//                                sketch = sketch,
//                                state = state
//                            ),
//                            contentDescription = "test image",
//                            modifier = Modifier.wrapContentSize()
//                        )
//                    }
//                }
//            }
//            waitUntil(timeoutMillis = 5000) {
//                stateHolder?.result is ImageResult.Success
//            }
//            assertTrue(actual = stateHolder?.result is ImageResult.Success)
//            assertEquals(
//                expected = IntSize(0, 0),
//                actual = (stateHolder?.result as ImageResult.Success).resize.size.toIntSize()
//            )
//        }
//
//        // width height is not bounded
//        runComposeUiTest {
//            var stateHolder: AsyncImageState? = null
//            var windowContainerSizeHolder: IntSize? = null
//            setContent {
//                windowContainerSizeHolder = windowContainerSize()
//                LifecycleContainer {
//                    Box(
//                        modifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState()).verticalScroll(
//                            rememberScrollState()
//                        )
//                    ) {
//                        val state = rememberAsyncImageState().apply {
//                            stateHolder = this
//                        }
//                        Image(
//                            painter = rememberAsyncImagePainter(
//                                uri = ResourceImages.jpeg.uri,
//                                sketch = sketch,
//                                state = state
//                            ),
//                            contentDescription = "test image",
//                            modifier = Modifier.wrapContentSize()
//                        )
//                    }
//                }
//            }
//            waitUntil(timeoutMillis = 5000) {
//                stateHolder?.result is ImageResult.Success
//            }
//            assertTrue(actual = stateHolder?.result is ImageResult.Success)
//            assertEquals(
//                expected = IntSize(0, 0),
//                actual = (stateHolder?.result as ImageResult.Success).resize.size.toIntSize()
//            )
//        }
    }
}