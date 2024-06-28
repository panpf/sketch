package com.github.panpf.sketch.animated.test.decode.internal

import com.github.panpf.sketch.decode.internal.isAnimatedHeif
import com.github.panpf.sketch.decode.internal.isAnimatedWebP
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.decode.internal.isHeif
import com.github.panpf.sketch.decode.internal.isWebP
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.fetch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnimatedDecodeUtilsTest {

    @Test
    fun testIsWebP() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val webpFetchResult = ImageRequest(context, ResourceImages.webp.uri).fetch(sketch)
        val animWebpFetchResult = ImageRequest(context, ResourceImages.animWebp.uri).fetch(sketch)
        val jpegFetchResult = ImageRequest(context, ResourceImages.jpeg.uri).fetch(sketch)

        assertTrue(webpFetchResult.headerBytes.isWebP())
        assertTrue(animWebpFetchResult.headerBytes.isWebP())
        assertFalse(jpegFetchResult.headerBytes.isWebP())
        assertFalse(webpFetchResult.headerBytes.copyOf().apply {
            set(8, 'V'.code.toByte())
        }.isWebP())
    }

    @Test
    fun testIsAnimatedWebP() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val webpFetchResult = ImageRequest(context, ResourceImages.webp.uri).fetch(sketch)
        val animWebpFetchResult = ImageRequest(context, ResourceImages.animWebp.uri).fetch(sketch)
        val jpegFetchResult = ImageRequest(context, ResourceImages.jpeg.uri).fetch(sketch)

        // test_error_webp_anim.webp is not animated webp, must use the RiffAnimChunk function to judge
        assertFalse(webpFetchResult.headerBytes.isAnimatedWebP())

        assertTrue(animWebpFetchResult.headerBytes.isAnimatedWebP())
        assertFalse(animWebpFetchResult.headerBytes.copyOf().apply {
            set(12, 'X'.code.toByte())
        }.isAnimatedWebP())

        assertTrue(animWebpFetchResult.headerBytes.isAnimatedWebP())
        assertFalse(animWebpFetchResult.headerBytes.copyOf().apply {
            set(16, 0)
        }.isAnimatedWebP())

        assertFalse(jpegFetchResult.headerBytes.isAnimatedWebP())
    }

    @Test
    fun testIsHeif() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val animWebpFetchResult = ImageRequest(context, ResourceImages.animWebp.uri).fetch(sketch)
        val jpegFetchResult = ImageRequest(context, ResourceImages.jpeg.uri).fetch(sketch)
        val heicFetchResult = ImageRequest(context, ResourceImages.heic.uri).fetch(sketch)

        assertTrue(heicFetchResult.headerBytes.isHeif())
        assertFalse(animWebpFetchResult.headerBytes.isHeif())
        assertFalse(jpegFetchResult.headerBytes.isHeif())
    }

    @Test
    fun testIsAnimatedHeif() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val animWebpFetchResult = ImageRequest(context, ResourceImages.animWebp.uri).fetch(sketch)
        val jpegFetchResult = ImageRequest(context, ResourceImages.jpeg.uri).fetch(sketch)
        val animHeifFetchResult = ImageRequest(context, ResourceImages.animHeif.uri).fetch(sketch)
        val heicFetchResult = ImageRequest(context, ResourceImages.heic.uri).fetch(sketch)

        assertTrue(animHeifFetchResult.headerBytes.isAnimatedHeif())

        assertTrue(animHeifFetchResult.headerBytes.copyOf().apply {
            set(8, 'h'.code.toByte())
            set(9, 'e'.code.toByte())
            set(10, 'v'.code.toByte())
            set(11, 'c'.code.toByte())
        }.isAnimatedHeif())

        assertTrue(animHeifFetchResult.headerBytes.copyOf().apply {
            set(8, 'h'.code.toByte())
            set(9, 'e'.code.toByte())
            set(10, 'v'.code.toByte())
            set(11, 'x'.code.toByte())
        }.isAnimatedHeif())

        assertFalse(heicFetchResult.headerBytes.isAnimatedHeif())
        assertFalse(animWebpFetchResult.headerBytes.isAnimatedHeif())
        assertFalse(jpegFetchResult.headerBytes.isAnimatedHeif())
    }

    @Test
    fun testIsGif() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val animWebpFetchResult = ImageRequest(context, ResourceImages.animWebp.uri).fetch(sketch)
        val animGifFetchResult = ImageRequest(context, ResourceImages.animGif.uri).fetch(sketch)

        assertTrue(animGifFetchResult.headerBytes.isGif())
        assertTrue(animGifFetchResult.headerBytes.copyOf().apply {
            set(4, '7'.code.toByte())
        }.isGif())
        assertFalse(animWebpFetchResult.headerBytes.isGif())
    }
}