package com.github.panpf.sketch.test.util

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.util.BitmapInfo
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.toBitmapInfo
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapInfoTest {

    @Test
    fun testProperties() {
        BitmapInfo(100, 200, 500003, RGB_565).apply {
            Assert.assertEquals(100, width)
            Assert.assertEquals(200, height)
            Assert.assertEquals(500003, byteCount)
            Assert.assertEquals(RGB_565, config)
        }

        BitmapInfo(300, 100, 52345234, null).apply {
            Assert.assertEquals(300, width)
            Assert.assertEquals(100, height)
            Assert.assertEquals(52345234, byteCount)
            Assert.assertEquals(null, config)
        }
    }

    @Test
    fun testToString() {
        BitmapInfo(100, 200, 500003, RGB_565).apply {
            Assert.assertEquals(
                "BitmapInfo(width=100, height=200, byteCount=${500003L.formatFileSize()}, config=RGB_565)",
                toString()
            )
        }
    }

    @Test
    fun testShortToString() {
        BitmapInfo(100, 200, 500003, RGB_565).apply {
            Assert.assertEquals(
                "BitmapInfo(100x200,${500003L.formatFileSize()},RGB_565)",
                toShortString()
            )
        }
    }

    @Test
    fun testToBitmapInfo() {
        Bitmap.createBitmap(300, 500, ARGB_8888).toBitmapInfo().apply {
            Assert.assertEquals(300, width)
            Assert.assertEquals(500, height)
            Assert.assertEquals(300 * 500 * 4, byteCount)
            Assert.assertEquals(ARGB_8888, config)
        }

        Bitmap.createBitmap(300, 500, RGB_565).toBitmapInfo().apply {
            Assert.assertEquals(300, width)
            Assert.assertEquals(500, height)
            Assert.assertEquals(300 * 500 * 2, byteCount)
            Assert.assertEquals(RGB_565, config)
        }
    }
}