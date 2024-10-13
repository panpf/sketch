package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.AnimatedImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.toLogString
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.ColorType.GRAY_8
import org.jetbrains.skia.Data
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AnimatedImageTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        AnimatedImage(codec).apply {
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
        AnimatedImage(codec).apply {
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
        AnimatedImage(codec).apply {
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
        AnimatedImage(codec).apply {
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
        AnimatedImage(codec).apply {
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
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        val codec2 = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        val element1 = AnimatedImage(codec1)
        val element11 = AnimatedImage(codec1)
        val element2 = AnimatedImage(codec2)
        val element3 = AnimatedImage(codec1, imageInfo = codec1.imageInfo.withColorType(GRAY_8))
        val element4 = AnimatedImage(codec1, repeatCount = 5)
        val element5 = AnimatedImage(codec1, cacheDecodeTimeoutFrame = true)
        val element6 = AnimatedImage(codec1, animationStartCallback = {})
        val element7 = AnimatedImage(codec1, animationEndCallback = {})

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = element3)
        assertNotEquals(illegal = element1, actual = element4)
        assertNotEquals(illegal = element1, actual = element5)
        assertNotEquals(illegal = element1, actual = element6)
        assertNotEquals(illegal = element1, actual = element7)
        assertNotEquals(illegal = element2, actual = element3)
        assertNotEquals(illegal = element2, actual = element4)
        assertNotEquals(illegal = element2, actual = element5)
        assertNotEquals(illegal = element2, actual = element6)
        assertNotEquals(illegal = element2, actual = element7)
        assertNotEquals(illegal = element3, actual = element4)
        assertNotEquals(illegal = element3, actual = element5)
        assertNotEquals(illegal = element3, actual = element6)
        assertNotEquals(illegal = element3, actual = element7)
        assertNotEquals(illegal = element4, actual = element5)
        assertNotEquals(illegal = element4, actual = element6)
        assertNotEquals(illegal = element4, actual = element7)
        assertNotEquals(illegal = element5, actual = element6)
        assertNotEquals(illegal = element5, actual = element7)
        assertNotEquals(illegal = element6, actual = element7)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element6.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element7.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element6.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element7.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element6.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element7.hashCode())
        assertNotEquals(illegal = element4.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element4.hashCode(), actual = element6.hashCode())
        assertNotEquals(illegal = element4.hashCode(), actual = element7.hashCode())
        assertNotEquals(illegal = element5.hashCode(), actual = element6.hashCode())
        assertNotEquals(illegal = element5.hashCode(), actual = element7.hashCode())
        assertNotEquals(illegal = element6.hashCode(), actual = element7.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        val animatedImage = AnimatedImage(codec)
        assertEquals(
            expected = "AnimatedImage(image=${codec.toLogString()}, shareable=false)",
            actual = animatedImage.toString()
        )
    }
}