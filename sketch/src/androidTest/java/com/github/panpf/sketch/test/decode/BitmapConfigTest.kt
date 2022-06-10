package com.github.panpf.sketch.test.decode

import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.BitmapConfig
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapConfigTest {

    @Test
    fun testIsLowQuality() {
        Assert.assertTrue(BitmapConfig.LowQuality.isLowQuality)
        Assert.assertFalse(BitmapConfig.HighQuality.isLowQuality)
        Assert.assertFalse(BitmapConfig(RGB_565).isLowQuality)
        Assert.assertFalse(BitmapConfig(ARGB_8888).isLowQuality)
    }

    @Test
    fun testIsHighQuality() {
        Assert.assertFalse(BitmapConfig.LowQuality.isHighQuality)
        Assert.assertTrue(BitmapConfig.HighQuality.isHighQuality)
        Assert.assertFalse(BitmapConfig(RGB_565).isHighQuality)
        Assert.assertFalse(BitmapConfig(ARGB_8888).isHighQuality)
    }

    @Test
    fun testIsFixed() {
        Assert.assertFalse(BitmapConfig.LowQuality.isFixed)
        Assert.assertFalse(BitmapConfig.HighQuality.isFixed)
        Assert.assertTrue(BitmapConfig(RGB_565).isFixed)
        Assert.assertTrue(BitmapConfig(ARGB_8888).isFixed)
    }

    @Test
    fun testIsDynamic() {
        Assert.assertTrue(BitmapConfig.LowQuality.isDynamic)
        Assert.assertTrue(BitmapConfig.HighQuality.isDynamic)
        Assert.assertFalse(BitmapConfig(RGB_565).isDynamic)
        Assert.assertFalse(BitmapConfig(ARGB_8888).isDynamic)
    }

    @Test
    fun testKey() {
        BitmapConfig.LowQuality.apply {
            Assert.assertEquals("BitmapConfig(LowQuality)", key)
        }
        BitmapConfig.HighQuality.apply {
            Assert.assertEquals("BitmapConfig(HighQuality)", key)
        }
        BitmapConfig(RGB_565).apply {
            Assert.assertEquals("BitmapConfig(RGB_565)", key)
        }
    }

    @Test
    fun testGetConfig() {
        BitmapConfig.LowQuality.apply {
            Assert.assertEquals(RGB_565, getConfig("image/jpeg"))
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                Assert.assertEquals(ARGB_4444, getConfig("image/png"))
            } else {
                Assert.assertEquals(ARGB_8888, getConfig("image/png"))
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                Assert.assertEquals(ARGB_4444, getConfig(null))
            } else {
                Assert.assertEquals(ARGB_8888, getConfig(null))
            }
        }

        BitmapConfig.HighQuality.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Assert.assertEquals(RGBA_F16, getConfig("image/jpeg"))
                Assert.assertEquals(RGBA_F16, getConfig("image/png"))
                Assert.assertEquals(RGBA_F16, getConfig(null))
            } else {
                Assert.assertEquals(ARGB_8888, getConfig("image/jpeg"))
                Assert.assertEquals(ARGB_8888, getConfig("image/png"))
                Assert.assertEquals(ARGB_8888, getConfig(null))
            }
        }

        BitmapConfig(RGB_565).apply {
            Assert.assertEquals(RGB_565, getConfig("image/jpeg"))
            Assert.assertEquals(RGB_565, getConfig("image/png"))
            Assert.assertEquals(RGB_565, getConfig(null))
        }
    }
}