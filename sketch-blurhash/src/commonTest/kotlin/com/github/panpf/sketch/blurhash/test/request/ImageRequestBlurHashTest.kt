package com.github.panpf.sketch.blurhash.test.request

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.BlurHashStateImage
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageRequestBlurHashTest {

    @Test
    fun testPlaceholder() {
        val context = getTestContext()
        ImageRequest(context, uri = "http://sample.com/sample.jpeg") {
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

    @Test
    fun testFallback() {
        val context = getTestContext()
        ImageRequest(context, uri = "http://sample.com/sample.jpeg") {
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

    @Test
    fun testError() {
        val context = getTestContext()
        ImageRequest(context, uri = "http://sample.com/sample.jpeg") {
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