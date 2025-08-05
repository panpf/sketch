package com.github.panpf.sketch.blurhash.test.request

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.request.ComposableImageOptions
import com.github.panpf.sketch.state.BlurhashStateImage
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ImageOptionsBlurhashTest {

    @Test
    fun testPlaceholder() {
        runComposeUiTest {
            setContent {
                ComposableImageOptions {
                    placeholder(BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(200, 100)))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(200, 100)),
                        actual = placeholder
                    )
                }

                ComposableImageOptions {
                    placeholder(BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"),
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
                ComposableImageOptions {
                    fallback(BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(200, 100)))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(200, 100)),
                        actual = fallback
                    )
                }

                ComposableImageOptions {
                    fallback(BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"),
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
                ComposableImageOptions {
                    error(BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(200, 100)))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(200, 100)),
                        actual = error
                    )
                }

                ComposableImageOptions {
                    error(BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"))
                }.apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"),
                        actual = error
                    )
                }
            }
        }
    }
}