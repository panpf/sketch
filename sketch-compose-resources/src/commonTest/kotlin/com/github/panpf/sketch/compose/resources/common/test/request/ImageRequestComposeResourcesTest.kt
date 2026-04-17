package com.github.panpf.sketch.compose.resources.common.test.request

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.images.Res
import com.github.panpf.sketch.images.moon
import com.github.panpf.sketch.painter.equitablePainterResource
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.fallback
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.state.PainterStateImage
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ImageRequestComposeResourcesTest {

    @Test
    fun testPlaceholder() {
        runComposeUiTest {
            setContent {
                ComposableImageRequest("http://sample.com/sample.jpeg") {
                    placeholder(Res.drawable.moon)
                }.apply {
                    assertEquals(
                        expected = PainterStateImage(equitablePainterResource(Res.drawable.moon)),
                        actual = placeholder
                    )
                }
            }
        }
    }

    @Test
    fun testFallback() {
        runComposeUiTest {
            setContent {
                ComposableImageRequest("http://sample.com/sample.jpeg") {
                    fallback(Res.drawable.moon)
                }.apply {
                    assertEquals(
                        expected = PainterStateImage(equitablePainterResource(Res.drawable.moon)),
                        actual = fallback
                    )
                }
            }
        }
    }

    @Test
    fun testError() {
        runComposeUiTest {
            setContent {
                ComposableImageRequest("http://sample.com/sample.jpeg") {
                    error(Res.drawable.moon)
                }.apply {
                    assertEquals(
                        expected = PainterStateImage(equitablePainterResource(Res.drawable.moon)),
                        actual = error
                    )
                }
            }
        }
    }
}