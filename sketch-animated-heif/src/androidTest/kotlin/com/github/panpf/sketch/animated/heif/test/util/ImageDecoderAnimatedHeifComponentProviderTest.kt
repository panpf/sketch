package com.github.panpf.sketch.animated.heif.test.util

import android.os.Build
import com.github.panpf.sketch.decode.ImageDecoderAnimatedHeifDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.ImageDecoderAnimatedHeifComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ImageDecoderAnimatedHeifComponentProviderTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = ImageDecoderAnimatedHeifComponentProvider()
        assertEquals(
            expected = null,
            actual = componentProvider.addFetchers(context)
        )
        assertEquals(
            expected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                listOf(ImageDecoderAnimatedHeifDecoder.Factory())
            } else {
                null
            },
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
        val element1 = ImageDecoderAnimatedHeifComponentProvider()
        val element11 = ImageDecoderAnimatedHeifComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ImageDecoderAnimatedHeifComponentProvider",
            actual = ImageDecoderAnimatedHeifComponentProvider().toString()
        )
    }
}