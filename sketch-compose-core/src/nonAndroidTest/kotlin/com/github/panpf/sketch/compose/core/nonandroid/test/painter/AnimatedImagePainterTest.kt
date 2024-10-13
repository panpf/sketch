package com.github.panpf.sketch.compose.core.nonandroid.test.painter

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.github.panpf.sketch.AnimatedImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.painter.AnimatedImagePainter
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.isActive
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AnimatedImagePainterTest {

    @Test
    fun testIntrinsicSize() {
        val context = getTestContext()
        val codec1 = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        val animatedImage = AnimatedImage(codec1)
        AnimatedImagePainter(animatedImage).apply {
            assertEquals(
                expected = IntSize(animatedImage.width, animatedImage.height).toSize(),
                actual = intrinsicSize
            )
        }
        AnimatedImagePainter(
            animatedImage,
            srcSize = IntSize(100, 100)
        ).apply {
            assertEquals(
                expected = IntSize(100, 100).toSize(),
                actual = intrinsicSize
            )
        }
    }

    @Test
    fun testRememberObserver() {
        val context = getTestContext()
        val codec1 = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        val animatedImage = AnimatedImage(codec1)
        val animatedImagePainter = AnimatedImagePainter(animatedImage)

        assertEquals(expected = 0, actual = animatedImagePainter.rememberedCounter.count)
        assertEquals(expected = null, actual = animatedImagePainter.coroutineScope?.isActive)

        animatedImagePainter.onRemembered()
        assertEquals(expected = 1, actual = animatedImagePainter.rememberedCounter.count)
        assertEquals(expected = true, actual = animatedImagePainter.coroutineScope?.isActive)

        animatedImagePainter.onRemembered()
        assertEquals(expected = 2, actual = animatedImagePainter.rememberedCounter.count)
        assertEquals(expected = true, actual = animatedImagePainter.coroutineScope?.isActive)

        animatedImagePainter.onRemembered()
        assertEquals(expected = 3, actual = animatedImagePainter.rememberedCounter.count)
        assertEquals(expected = true, actual = animatedImagePainter.coroutineScope?.isActive)

        animatedImagePainter.onAbandoned()
        assertEquals(expected = 2, actual = animatedImagePainter.rememberedCounter.count)
        assertEquals(expected = true, actual = animatedImagePainter.coroutineScope?.isActive)

        animatedImagePainter.onForgotten()
        assertEquals(expected = 1, actual = animatedImagePainter.rememberedCounter.count)
        assertEquals(expected = true, actual = animatedImagePainter.coroutineScope?.isActive)

        animatedImagePainter.onForgotten()
        assertEquals(expected = 0, actual = animatedImagePainter.rememberedCounter.count)
        assertEquals(expected = null, actual = animatedImagePainter.coroutineScope?.isActive)

        animatedImagePainter.onForgotten()
        assertEquals(expected = 0, actual = animatedImagePainter.rememberedCounter.count)
        assertEquals(expected = null, actual = animatedImagePainter.coroutineScope?.isActive)
    }

    @Test
    fun testStartStopIsRunning() {
        val context = getTestContext()
        val codec1 = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        val animatedImage = AnimatedImage(codec1)
        val animatedImagePainter = AnimatedImagePainter(animatedImage)

        assertEquals(false, animatedImagePainter.isRunning())

        animatedImagePainter.start()
        assertEquals(false, animatedImagePainter.isRunning())

        animatedImagePainter.stop()
        assertEquals(false, animatedImagePainter.isRunning())

        animatedImagePainter.onRemembered()
        assertEquals(true, animatedImagePainter.isRunning())

        animatedImagePainter.stop()
        assertEquals(false, animatedImagePainter.isRunning())

        animatedImagePainter.start()
        assertEquals(true, animatedImagePainter.isRunning())

        animatedImagePainter.onForgotten()
        assertEquals(false, animatedImagePainter.isRunning())
    }

    @Test
    fun testOnDraw() {
        // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
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
        val animatedImage1 = AnimatedImage(codec1)
        val animatedImage2 = AnimatedImage(codec2)
        val element1 = AnimatedImagePainter(animatedImage1)
        val element11 = AnimatedImagePainter(animatedImage1)
        val element2 = AnimatedImagePainter(animatedImage2)
        val element3 = AnimatedImagePainter(animatedImage1, srcOffset = IntOffset(2, 2))
        val element4 = AnimatedImagePainter(animatedImage1, srcSize = IntSize(100, 100))
        val element5 = AnimatedImagePainter(animatedImage1, filterQuality = FilterQuality.High)

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
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        val animatedImage = AnimatedImage(codec)
        val animatedImagePainter = AnimatedImagePainter(animatedImage)
        assertEquals(
            expected = "AnimatedImagePainter(animatedImage=$animatedImage, srcOffset=${IntOffset.Zero}, srcSize=${
                IntSize(
                    animatedImage.width,
                    animatedImage.height
                )
            }, filterQuality=${DefaultFilterQuality})",
            actual = animatedImagePainter.toString()
        )
    }
}