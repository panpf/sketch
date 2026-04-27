package com.github.panpf.sketch.video.ffmpeg.test.util

import com.github.panpf.sketch.decode.FFmpegVideoFrameDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.FFmpegVideoComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FFmpegVideoComponentProviderTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = FFmpegVideoComponentProvider()
        assertEquals(
            expected = null,
            actual = componentProvider.addFetchers(context)
        )
        assertEquals(
            expected = listOf(FFmpegVideoFrameDecoder.Factory()),
            actual = componentProvider.addDecoders(context)
        )
        assertEquals(
            expected = null,
            actual = componentProvider.addInterceptors(context)
        )
        assertEquals(
            expected = null,
            actual = componentProvider.disabledFetchers(context)
        )
        assertEquals(
            expected = null,
            actual = componentProvider.disabledDecoders(context)
        )
        assertEquals(
            expected = null,
            actual = componentProvider.disabledInterceptors(context)
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = FFmpegVideoComponentProvider()
        val element11 = FFmpegVideoComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "FFmpegVideoComponentProvider",
            actual = FFmpegVideoComponentProvider().toString()
        )
    }
}