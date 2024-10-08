@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.request

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.request.ComposableImageOptions
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.fallback
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.request.resize
import com.github.panpf.sketch.request.size
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.rememberColorPainterStateImage
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageOptionsComposeCommonTest {

    @Test
    fun testComposableImageOptions() {
        runComposeUiTest {
            setContent {
                ComposableImageOptions {
                    placeholder(rememberColorPainterStateImage(Color.Yellow))
                }.apply {
                    assertEquals(
                        expected = ImageOptions {
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
        ImageOptions {
            resize(IntSize(101, 202))
        }.apply {
            assertEquals(
                expected = ImageOptions {
                    resize(Size(101, 202))
                },
                actual = this
            )
        }

        ImageOptions {
            resize(
                size = IntSize(101, 202),
                precision = Precision.EXACTLY,
                scale = Scale.END_CROP
            )
        }.apply {
            assertEquals(
                expected = ImageOptions {
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
        ImageOptions {
            size(IntSize(101, 202))
        }.apply {
            assertEquals(
                expected = ImageOptions {
                    size(Size(101, 202))
                },
                actual = this
            )
        }
    }

    @Test
    fun testPlaceholder() {
        ImageOptions {
            placeholder(Color.Gray)
        }.apply {
            assertEquals(
                expected = ImageOptions {
                    placeholder(ColorPainterStateImage(Color.Gray))
                },
                actual = this
            )
        }
    }

    @Test
    fun testFallback() {
        ImageOptions {
            fallback(Color.Yellow)
        }.apply {
            assertEquals(
                expected = ImageOptions {
                    fallback(ColorPainterStateImage(Color.Yellow))
                },
                actual = this
            )
        }
    }

    @Test
    fun testError() {
        ImageOptions {
            error(Color.Red)
        }.apply {
            assertEquals(
                expected = ImageOptions {
                    error(ColorPainterStateImage(Color.Red))
                },
                actual = this
            )
        }
    }
}