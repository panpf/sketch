package com.github.panpf.sketch.blurhash.test.request

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.state.BlurhashStateImage
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ImageRequestBlurhashTest {

    @Test
    fun testPlaceholder() {
        runComposeUiTest {
            setContent {
                ComposableImageRequest(uri = "http://sample.com/sample.jpeg") {
                    placeholder(BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)),
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
                    fallback(BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)), actual = fallback
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
                    error(BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)), actual = error
                    )
                }
            }
        }
    }
}