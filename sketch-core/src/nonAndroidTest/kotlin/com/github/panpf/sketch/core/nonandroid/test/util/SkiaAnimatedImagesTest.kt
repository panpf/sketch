package com.github.panpf.sketch.core.nonandroid.test.util

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.toLogString
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import kotlin.test.Test
import kotlin.test.assertEquals

class SkiaAnimatedImagesTest {

    @Test
    fun testCodecToLogString() {
        val context = getTestContext()
        val bytes = ResourceImages.jpeg.toDataSource(context)
            .openSource().buffer()
            .use { it.readByteArray() }
        val data = Data.makeFromBytes(bytes)
        val codec = Codec.makeFromData(data)
        assertEquals(
            expected = "Codec@${codec.toHexString()}(1291x1936,RGBA_8888,sRGB)",
            actual = codec.toLogString()
        )
    }
}