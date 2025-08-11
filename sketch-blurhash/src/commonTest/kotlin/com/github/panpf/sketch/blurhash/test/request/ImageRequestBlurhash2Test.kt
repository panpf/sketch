package com.github.panpf.sketch.blurhash.test.request

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.state.BlurHashStateImage
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ImageRequestBlurhash2Test {

    @Test
    fun testPlaceholder() {
        runComposeUiTest {
            setContent {
                ComposableImageRequest(uri = "http://sample.com/sample.jpeg") {
                    placeholder(BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)))
                }.apply {
                    assertEquals(
                        expected = BlurHashStateImage(
                            "L6PZfSi_.AyE_3t7t7R**0o#DgR4",
                            Size(100, 200)
                        ),
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
                ComposableImageRequest(uri = "http://sample.com/sample.jpeg") {
                    fallback(BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)))
                }.apply {
                    assertEquals(
                        expected = BlurHashStateImage(
                            "L6PZfSi_.AyE_3t7t7R**0o#DgR4",
                            Size(100, 200)
                        ), actual = fallback
                    )
                }
            }
        }
    }

    @Test
    fun testError() {
        runComposeUiTest {
            setContent {
                ComposableImageRequest(uri = "http://sample.com/sample.jpeg") {
                    error(BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)))
                }.apply {
                    assertEquals(
                        expected = BlurHashStateImage(
                            "L6PZfSi_.AyE_3t7t7R**0o#DgR4",
                            Size(100, 200)
                        ), actual = error
                    )
                }
            }
        }
    }
}