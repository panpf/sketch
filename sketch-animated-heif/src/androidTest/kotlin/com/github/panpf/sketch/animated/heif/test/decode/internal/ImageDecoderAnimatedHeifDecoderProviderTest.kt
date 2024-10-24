package com.github.panpf.sketch.animated.heif.test.decode.internal

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.decode.ImageDecoderAnimatedHeifDecoder
import com.github.panpf.sketch.decode.internal.ImageDecoderAnimatedHeifDecoderProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageDecoderAnimatedHeifDecoderProviderTest {

    @Test
    fun testFactory() {
        val context = getTestContext()
        val decoderProvider = ImageDecoderAnimatedHeifDecoderProvider()
        val decoderFactory = decoderProvider.factory(context)
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            assertTrue(
                actual = decoderFactory is ImageDecoderAnimatedHeifDecoder.Factory,
                message = decoderFactory.toString()
            )
        } else {
            assertNull(decoderFactory)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ImageDecoderAnimatedHeifDecoderProvider()
        val element11 = ImageDecoderAnimatedHeifDecoderProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val decoderProvider = ImageDecoderAnimatedHeifDecoderProvider()
        assertTrue(
            actual = decoderProvider.toString().contains("ImageDecoderAnimatedHeifDecoderProvider"),
            message = decoderProvider.toString()
        )
        assertTrue(
            actual = decoderProvider.toString().contains("@"),
            message = decoderProvider.toString()
        )
    }
}