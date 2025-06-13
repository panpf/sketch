package com.github.panpf.sketch.animated.core.nonandroid.test

import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.toLogString
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Data
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SkiaAnimatedImageTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.Companion.makeFromBytes(it) }
            .let { Codec.Companion.makeFromData(it) }
        SkiaAnimatedImage(codec).apply {
            assertSame(expected = codec, actual = codec)
        }
    }

    @Test
    fun testWidthHeight() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.Companion.makeFromBytes(it) }
            .let { Codec.Companion.makeFromData(it) }
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
            .let { Data.Companion.makeFromBytes(it) }
            .let { Codec.Companion.makeFromData(it) }
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
            .let { Data.Companion.makeFromBytes(it) }
            .let { Codec.Companion.makeFromData(it) }
        SkiaAnimatedImage(codec).apply {
            assertFalse(actual = shareable)
        }
    }

    @Test
    fun testCheckValid() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.Companion.makeFromBytes(it) }
            .let { Codec.Companion.makeFromData(it) }
        SkiaAnimatedImage(codec).apply {
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val codec1 = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.Companion.makeFromBytes(it) }
            .let { Codec.Companion.makeFromData(it) }
        val codec2 = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.Companion.makeFromBytes(it) }
            .let { Codec.Companion.makeFromData(it) }
        val element1 = SkiaAnimatedImage(codec1)
        val element11 = SkiaAnimatedImage(codec1)
        val element2 = SkiaAnimatedImage(codec2)
        val element3 = SkiaAnimatedImage(
            codec = codec1,
            colorInfo = codec1.imageInfo.withColorType(ColorType.GRAY_8).colorInfo
        )
        val element4 = SkiaAnimatedImage(codec1, repeatCount = 5)
        val element5 = SkiaAnimatedImage(codec1, cacheDecodeTimeoutFrame = true)

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = element3)
        assertNotEquals(illegal = element1, actual = element4)
        assertNotEquals(illegal = element1, actual = element5)
        assertNotEquals(illegal = element2, actual = element3)
        assertNotEquals(illegal = element2, actual = element4)
        assertNotEquals(illegal = element2, actual = element5)
        assertNotEquals(illegal = element3, actual = element4)
        assertNotEquals(illegal = element3, actual = element5)
        assertNotEquals(illegal = element4, actual = element5)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element4.hashCode(), actual = element5.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.Companion.makeFromBytes(it) }
            .let { Codec.Companion.makeFromData(it) }
        val animatedImage = SkiaAnimatedImage(codec)
        assertEquals(
            expected = "SkiaAnimatedImage(image=${codec.toLogString()}, colorInfo=${codec.colorInfo.toLogString()}, repeatCount=-1, cacheDecodeTimeoutFrame=false)",
            actual = animatedImage.toString()
        )
    }
}