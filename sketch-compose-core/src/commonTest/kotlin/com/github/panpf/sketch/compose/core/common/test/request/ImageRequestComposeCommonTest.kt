@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.request

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.fallback
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.request.resize
import com.github.panpf.sketch.request.size
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.rememberColorPainterStateImage
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageRequestComposeCommonTest {

    @Test
    fun testComposableImageRequest() {
        runComposeUiTest {
            setContent {
                val context = LocalPlatformContext.current
                ComposableImageRequest(context, "http://sample.com/sample.jpeg").apply {
                    assertEquals(
                        expected = ImageRequest(context, "http://sample.com/sample.jpeg"),
                        actual = this
                    )
                }

                ComposableImageRequest(context, "http://sample.com/sample.jpeg") {
                    placeholder(rememberColorPainterStateImage(Color.Yellow))
                }.apply {
                    assertEquals(
                        expected = ImageRequest(context, "http://sample.com/sample.jpeg") {
                            placeholder(ColorPainterStateImage(Color.Yellow))
                        },
                        actual = this
                    )
                }

                ComposableImageRequest("http://sample.com/sample.jpeg").apply {
                    assertEquals(
                        expected = ImageRequest(context, "http://sample.com/sample.jpeg"),
                        actual = this
                    )
                }

                ComposableImageRequest("http://sample.com/sample.jpeg") {
                    placeholder(rememberColorPainterStateImage(Color.Yellow))
                }.apply {
                    assertEquals(
                        expected = ImageRequest(context, "http://sample.com/sample.jpeg") {
                            placeholder(ColorPainterStateImage(Color.Yellow))
                        },
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testResize() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            resize(IntSize(101, 202))
        }.apply {
            assertEquals(
                expected = ImageRequest(context, "http://sample.com/sample.jpeg") {
                    resize(Size(101, 202))
                },
                actual = this
            )
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            resize(
                size = IntSize(101, 202),
                precision = Precision.EXACTLY,
                scale = Scale.END_CROP
            )
        }.apply {
            assertEquals(
                expected = ImageRequest(context, "http://sample.com/sample.jpeg") {
                    resize(
                        size = Size(101, 202),
                        precision = Precision.EXACTLY,
                        scale = Scale.END_CROP
                    )
                },
                actual = this
            )
        }
    }

    @Test
    fun testSize() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            size(IntSize(101, 202))
        }.apply {
            assertEquals(
                expected = ImageRequest(context, "http://sample.com/sample.jpeg") {
                    size(Size(101, 202))
                },
                actual = this
            )
        }
    }

    @Test
    fun testPlaceholder() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            placeholder(Color.Gray)
        }.apply {
            assertEquals(
                expected = ImageRequest(context, "http://sample.com/sample.jpeg") {
                    placeholder(ColorPainterStateImage(Color.Gray))
                },
                actual = this
            )
        }
    }

    @Test
    fun testFallback() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            fallback(Color.Yellow)
        }.apply {
            assertEquals(
                expected = ImageRequest(context, "http://sample.com/sample.jpeg") {
                    fallback(ColorPainterStateImage(Color.Yellow))
                },
                actual = this
            )
        }
    }

    @Test
    fun testError() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            error(Color.Red)
        }.apply {
            assertEquals(
                expected = ImageRequest(context, "http://sample.com/sample.jpeg") {
                    error(ColorPainterStateImage(Color.Red))
                },
                actual = this
            )
        }
    }
}