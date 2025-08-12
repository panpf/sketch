package com.github.panpf.sketch.blurhash.common.test.request

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.blurHashError
import com.github.panpf.sketch.request.blurHashFallback
import com.github.panpf.sketch.request.blurHashPlaceholder
import com.github.panpf.sketch.state.BlurHashStateImage
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageRequestBlurHashTest {

    private val uri = "http://sample.com/sample.jpeg"
    private val blurHash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"

    @Test
    fun testBlurHashPlaceholder() {
        val context = getTestContext()

        ImageRequest(context, uri = uri) {
            placeholder(BlurHashStateImage(blurHash))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = placeholder
            )
        }

        ImageRequest(context, uri = uri) {
            blurHashPlaceholder(blurHash)
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = placeholder
            )
        }

        ImageRequest(context, uri = uri) {
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
        val context = getTestContext()

        ImageRequest(context, uri = uri) {
            fallback(BlurHashStateImage(blurHash))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = fallback
            )
        }

        ImageRequest(context, uri = uri) {
            blurHashFallback(blurHash)
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = fallback
            )
        }

        ImageRequest(context, uri = uri) {
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
        val context = getTestContext()

        ImageRequest(context, uri = uri) {
            error(BlurHashStateImage(blurHash))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = error
            )
        }

        ImageRequest(context, uri = uri) {
            blurHashError(blurHash)
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash),
                actual = error
            )
        }

        ImageRequest(context, uri = uri) {
            blurHashError(blurHash, Size(100, 200))
        }.apply {
            assertEquals(
                expected = BlurHashStateImage(blurHash, Size(100, 200)),
                actual = error
            )
        }
    }
}