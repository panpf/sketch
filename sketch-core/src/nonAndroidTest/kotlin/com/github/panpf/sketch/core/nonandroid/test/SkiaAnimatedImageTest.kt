package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.toLogString
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SkiaAnimatedImageTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        SkiaAnimatedImage(codec).apply {
            assertSame(expected = codec, actual = codec)
        }
    }

    @Test
    fun testWidthHeight() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        SkiaAnimatedImage(codec).apply {
            assertEquals(expected = 480, actual = width)
            assertEquals(expected = 480, actual = height)
        }
    }

    @Test
    fun testByteCount() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        SkiaAnimatedImage(codec).apply {
            assertEquals(
                expected = 480 * 480 * codec.imageInfo.bytesPerPixel.toLong(),
                actual = byteCount
            )
        }
    }

    @Test
    fun testShareable() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        SkiaAnimatedImage(codec).apply {
            assertFalse(actual = shareable)
        }
    }

    @Test
    fun testCheckValid() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        SkiaAnimatedImage(codec).apply {
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
        }
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        val skiaAnimatedImage = SkiaAnimatedImage(codec)
        assertEquals(
            expected = "SkiaAnimatedImage(image=${codec.toLogString()}, shareable=false)",
            actual = skiaAnimatedImage.toString()
        )
    }
}