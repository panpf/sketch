package com.github.panpf.sketch.compose.core.common.test

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.LifecycleContainer
import com.github.panpf.sketch.util.toIntSize
import com.github.panpf.sketch.util.windowContainerSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class AsyncImageTest {

    @Test
    fun testAsyncImage1() {
        val (_, sketch) = getTestContextAndSketch()
        runComposeUiTest {
            setContent {
                LifecycleContainer {
                    AsyncImage(
                        uri = ResourceImages.jpeg.uri,
                        sketch = sketch,
                        contentDescription = "test image"
                    )

                    AsyncImage(
                        uri = ResourceImages.jpeg.uri,
                        sketch = sketch,
                        contentDescription = "test image",
                        modifier = Modifier,
                        state = rememberAsyncImageState(),
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.Red),
                        filterQuality = FilterQuality.High,
                        clipToBounds = false
                    )

                    AsyncImage(
                        uri = ResourceImages.jpeg.uri,
                        sketch = sketch,
                        contentDescription = "test image",
                        modifier = Modifier,
                        state = rememberAsyncImageState(),
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.Red),
                        filterQuality = FilterQuality.High,
                        clipToBounds = false
                    )
                }
            }

            // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
        }
    }

    @Test
    fun testAsyncImage2() {
        val (_, sketch) = getTestContextAndSketch()
        runComposeUiTest {
            setContent {
                LifecycleContainer {
                    AsyncImage(
                        request = ComposableImageRequest(uri = ResourceImages.jpeg.uri),
                        sketch = sketch,
                        contentDescription = "test image"
                    )

                    AsyncImage(
                        request = ComposableImageRequest(uri = ResourceImages.jpeg.uri),
                        sketch = sketch,
                        contentDescription = "test image",
                        modifier = Modifier,
                        state = rememberAsyncImageState(),
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.Red),
                        filterQuality = FilterQuality.High,
                        clipToBounds = false
                    )

                    AsyncImage(
                        request = ComposableImageRequest(ResourceImages.jpeg.uri),
                        sketch = sketch,
                        contentDescription = "test image",
                        modifier = Modifier,
                        state = rememberAsyncImageState(),
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.Red),
                        filterQuality = FilterQuality.High,
                        clipToBounds = false
                    )
                }
            }

            // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
        }
    }

    @Test
    fun testAsyncImageWrapContent() {
        val (_, sketch) = getTestContextAndSketch()

        runComposeUiTest {
            var stateHolder: AsyncImageState? = null
            var windowContainerSizeHolder: IntSize? = null
            setContent {
                windowContainerSizeHolder = windowContainerSize()
                LifecycleContainer {
                    val state = rememberAsyncImageState().apply {
                        stateHolder = this
                    }
                    AsyncImage(
                        uri = ResourceImages.jpeg.uri,
                        sketch = sketch,
                        state = state,
                        contentDescription = "test image"
                    )
                }
            }
            waitUntil {
                stateHolder?.result is ImageResult.Success
            }
            assertTrue(actual = stateHolder?.result is ImageResult.Success)
            assertEquals(
                expected = windowContainerSizeHolder,
                actual = (stateHolder?.result as ImageResult.Success).resize.size.toIntSize()
            )
        }
    }

    @Test
    fun testAsyncImageWrapContent2() {
        val (_, sketch) = getTestContextAndSketch()

        runComposeUiTest {
            var stateHolder: AsyncImageState? = null
            var windowContainerSizeHolder: IntSize? = null
            setContent {
                windowContainerSizeHolder = windowContainerSize()
                LifecycleContainer {
                    val state = rememberAsyncImageState().apply {
                        stateHolder = this
                    }
                    AsyncImage(
                        uri = ResourceImages.jpeg.uri,
                        sketch = sketch,
                        state = state,
                        contentDescription = "test image",
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
            waitUntil {
                stateHolder?.result is ImageResult.Success
            }
            assertTrue(actual = stateHolder?.result is ImageResult.Success)
            assertEquals(
                expected = windowContainerSizeHolder,
                actual = (stateHolder?.result as ImageResult.Success).resize.size.toIntSize()
            )
        }
    }
}