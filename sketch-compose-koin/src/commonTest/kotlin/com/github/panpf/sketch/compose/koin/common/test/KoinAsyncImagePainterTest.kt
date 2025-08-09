@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.koin.common.test

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.AsyncImagePainter
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.rememberAsyncImagePainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.test.utils.Koins
import com.github.panpf.sketch.test.utils.LifecycleContainer
import org.koin.mp.KoinPlatform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalTestApi::class)
class KoinAsyncImagePainterTest {

    init {
        Koins.initial()
    }

    @Test
    fun testRememberAsyncImagePainter() {
        runComposeUiTest {
            var painter1: AsyncImagePainter? = null
            var painter2: AsyncImagePainter? = null
            var painter3: AsyncImagePainter? = null
            var imageState2: AsyncImageState? = null
            var imageState3: AsyncImageState? = null
            setContent {
                LifecycleContainer {
                    painter1 = rememberAsyncImagePainter(uri = "https://www.test.com/test.jpg")

                    painter2 = rememberAsyncImagePainter(
                        uri = "https://www.test.com/test.jpg",
                        state = rememberAsyncImageState().apply { imageState2 = this },
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.High
                    )

                    painter3 = rememberAsyncImagePainter(
                        uri = "https://www.test.com/test.jpg",
                        state = rememberAsyncImageState().apply { imageState3 = this },
                        contentScale = ContentScale.Inside,
                        alignment = Alignment.BottomEnd,
                        filterQuality = FilterQuality.Medium
                    )
                }
            }
            waitForIdle()

            val sketch = KoinPlatform.getKoin().get<Sketch>()
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
        runComposeUiTest {
            var painter1: AsyncImagePainter? = null
            var painter2: AsyncImagePainter? = null
            var painter3: AsyncImagePainter? = null
            var imageState2: AsyncImageState? = null
            var imageState3: AsyncImageState? = null
            setContent {
                LifecycleContainer {
                    painter1 = rememberAsyncImagePainter(
                        request = ComposableImageRequest(uri = "https://www.test.com/test.jpg")
                    )

                    painter2 = rememberAsyncImagePainter(
                        request = ComposableImageRequest(uri = "https://www.test.com/test.jpg"),
                        state = rememberAsyncImageState().apply { imageState2 = this },
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.High
                    )

                    painter3 = rememberAsyncImagePainter(
                        request = ComposableImageRequest("https://www.test.com/test.jpg"),
                        state = rememberAsyncImageState().apply { imageState3 = this },
                        contentScale = ContentScale.Inside,
                        alignment = Alignment.BottomEnd,
                        filterQuality = FilterQuality.Medium
                    )
                }
            }
            waitForIdle()

            val sketch = KoinPlatform.getKoin().get<Sketch>()
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
}