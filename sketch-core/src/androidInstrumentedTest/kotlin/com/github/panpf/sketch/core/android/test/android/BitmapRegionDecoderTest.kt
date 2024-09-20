package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory.Options
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newBitmapRegionDecoderInstanceCompat
import com.github.panpf.sketch.util.Size
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BitmapRegionDecoderTest {

    @Test
    fun testMutable() {
        val context = getTestContext()
        val imageName = ResourceImages.jpeg.resourceName
        val imageSize = Size(1291, 1936)

        val options = Options()
        assertFalse(options.inMutable)
        val bitmap = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        assertFalse(bitmap.isMutable)

        options.inMutable = true
        assertTrue(options.inMutable)
        val bitmap1 = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        assertFalse(bitmap1.isMutable)
    }

    @Test
    fun testInPreferredConfig() {
        val context = getTestContext()
        val imageName = ResourceImages.jpeg.resourceName
        val imageSize = Size(1291, 1936)

        val options = Options()
        assertEquals(ARGB_8888, options.inPreferredConfig)
        val bitmap = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        assertEquals(ARGB_8888, bitmap.config)

        @Suppress("DEPRECATION")
        options.inPreferredConfig = ARGB_4444
        @Suppress("DEPRECATION")
        assertEquals(ARGB_4444, options.inPreferredConfig)
        val bitmap1 = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        if (VERSION.SDK_INT > VERSION_CODES.M) {
            assertEquals(ARGB_8888, bitmap1.config)
        } else {
            @Suppress("DEPRECATION")
            assertEquals(ARGB_4444, bitmap1.config)
        }
    }

    private fun <R> BitmapRegionDecoder.use(block: BitmapRegionDecoder.() -> R): R {
        try {
            return block(this)
        } finally {
            recycle()
        }
    }
}