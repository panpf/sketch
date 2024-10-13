package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BitmapFactoryTest {

    // TODO test format

    @Test
    fun testMutable() {
        val context = getTestContext()
        val imageName = ResourceImages.jpeg.resourceName

        val options = Options()
        assertFalse(options.inMutable)
        val bitmap = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        assertFalse(bitmap.isMutable)

        options.inMutable = true
        assertTrue(options.inMutable)
        val bitmap1 = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        assertTrue(bitmap1.isMutable)
    }

    @Test
    fun testInPreferredConfig() {
        val context = getTestContext()
        val imageName = ResourceImages.jpeg.resourceName

        val bitmap0 = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, null)
        }!!
        assertEquals(ARGB_8888, bitmap0.config)

        val options = Options()
        assertEquals(ARGB_8888, options.inPreferredConfig)
        val bitmap = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        assertEquals(ARGB_8888, bitmap.config)

        options.inPreferredConfig = ARGB_4444
        assertEquals(ARGB_4444, options.inPreferredConfig)
        val bitmap1 = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        if (VERSION.SDK_INT > VERSION_CODES.M) {
            assertEquals(ARGB_8888, bitmap1.config)
        } else {
            assertEquals(ARGB_4444, bitmap1.config)
        }
    }

    @Test
    fun testHasAlpha() {
        val context = getTestContext()

        context.assets.open(ResourceImages.jpeg.resourceName).use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            assertEquals(ARGB_8888, config)
            assertFalse(hasAlpha())
        }

        context.assets.open(ResourceImages.png.resourceName).use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            assertEquals(ARGB_8888, config)
            assertTrue(hasAlpha())
        }
    }
}