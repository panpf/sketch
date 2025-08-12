package com.github.panpf.sketch.blurhash.common.test.request

import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.blurHashError
import com.github.panpf.sketch.request.blurHashFallback
import com.github.panpf.sketch.request.blurHashPlaceholder
import com.github.panpf.sketch.state.BlurHashStateImage
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageOptionsBlurHashTest {

    private val blurHash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"

    @Test
    fun testBlurHashPlaceholder() {
        ImageOptions {
            placeholder(BlurHashStateImage(blurHash))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = placeholder
            )
        }

        ImageOptions {
            blurHashPlaceholder(blurHash)
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = placeholder
            )
        }

        ImageOptions {
            blurHashPlaceholder(blurHash, Size(100, 200))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash, Size(100, 200)),
                actual = placeholder
            )
        }
    }

    @Test
    fun testBlurHashFallback() {
        ImageOptions {
            fallback(BlurHashStateImage(blurHash))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = fallback
            )
        }

        ImageOptions {
            blurHashFallback(blurHash)
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = fallback
            )
        }

        ImageOptions {
            blurHashFallback(blurHash, Size(100, 200))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash, Size(100, 200)),
                actual = fallback
            )
        }
    }

    @Test
    fun testBlurHashError() {
        ImageOptions {
            error(BlurHashStateImage(blurHash))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = error
            )
        }

        ImageOptions {
            blurHashError(blurHash)
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = error
            )
        }

        ImageOptions {
            blurHashError(blurHash, Size(100, 200))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash, Size(100, 200)),
                actual = error
            )
        }
    }
}