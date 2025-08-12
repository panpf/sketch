package com.github.panpf.sketch.blurhash.test.request

import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.state.BlurHashStateImage
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageOptionsBlurHashTest {

    @Test
    fun testPlaceholder() {
        ImageOptions {
            placeholder(BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(200, 100)))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(
                    "L6PZfSi_.AyE_3t7t7R**0o#DgR4",
                    Size(200, 100)
                ),
                actual = placeholder
            )
        }

        ImageOptions {
            placeholder(BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"),
                actual = placeholder
            )
        }
    }

    @Test
    fun testFallback() {
        ImageOptions {
            fallback(BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(200, 100)))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(
                    "L6PZfSi_.AyE_3t7t7R**0o#DgR4",
                    Size(200, 100)
                ),
                actual = fallback
            )
        }

        ImageOptions {
            fallback(BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"),
                actual = fallback
            )
        }
    }

    @Test
    fun testError() {
        ImageOptions {
            error(BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(200, 100)))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(
                    "L6PZfSi_.AyE_3t7t7R**0o#DgR4",
                    Size(200, 100)
                ),
                actual = error
            )
        }

        ImageOptions {
            error(BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"),
                actual = error
            )
        }
    }
}