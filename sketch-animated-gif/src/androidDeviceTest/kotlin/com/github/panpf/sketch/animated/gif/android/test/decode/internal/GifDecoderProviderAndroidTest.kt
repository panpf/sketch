package com.github.panpf.sketch.animated.gif.android.test.decode.internal

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.decode.ImageDecoderGifDecoder
import com.github.panpf.sketch.decode.MovieGifDecoder
import com.github.panpf.sketch.decode.internal.GifDecoderProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class GifDecoderProviderAndroidTest {

    @Test
    fun testFactory() {
        val context = getTestContext()
        val decoderProvider = GifDecoderProvider()
        val decoderFactory = decoderProvider.factory(context)
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            assertTrue(
                actual = decoderFactory is ImageDecoderGifDecoder.Factory,
                message = decoderFactory.toString()
            )
        } else {
            assertTrue(
                actual = decoderFactory is MovieGifDecoder.Factory,
                message = decoderFactory.toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = GifDecoderProvider()
        val element11 = GifDecoderProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val decoderProvider = GifDecoderProvider()
        assertTrue(
            actual = decoderProvider.toString().contains("GifDecoderProvider"),
            message = decoderProvider.toString()
        )
        assertTrue(
            actual = decoderProvider.toString().contains("@"),
            message = decoderProvider.toString()
        )
    }
}