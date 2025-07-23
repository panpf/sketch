package com.github.panpf.sketch.blurhash.test.request

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.state.BlurhashStateImage
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.current
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ImageRequestBlurhashTest {

    @Test
    fun testPlaceholder() {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return
        }
        runComposeUiTest {
            setContent {
                ComposableImageRequest(uri = "blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4") {
                    placeholder(BlurhashStateImage(""))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4"),
                        actual = placeholder
                    )
                }
            }
        }
    }

    @Test
    fun testFallback() {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return
        }
        runComposeUiTest {
            setContent {
                ComposableImageRequest(uri = "blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4") {
                    fallback(BlurhashStateImage(""))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4"),
                        actual = fallback
                    )
                }
            }
        }
    }

    @Test
    fun testError() {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return
        }
        runComposeUiTest {
            setContent {
                ComposableImageRequest(uri = "blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4") {
                    error(BlurhashStateImage(""))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4"),
                        actual = error
                    )
                }
            }
        }
    }
}