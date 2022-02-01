package com.github.panpf.sketch.test.decode

import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.os.Build
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.BitmapConfig
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapConfigTest {

    @Test
    fun testQualityLevel() {
        BitmapConfig.LOW_QUALITY.apply {
            Assert.assertTrue(isLowQuality)
            Assert.assertFalse(isMiddenQuality)
            Assert.assertFalse(isHighQuality)
        }
        BitmapConfig.MIDDEN_QUALITY.apply {
            Assert.assertFalse(isLowQuality)
            Assert.assertTrue(isMiddenQuality)
            Assert.assertFalse(isHighQuality)
        }
        BitmapConfig.HIGH_QUALITY.apply {
            Assert.assertFalse(isLowQuality)
            Assert.assertFalse(isMiddenQuality)
            Assert.assertTrue(isHighQuality)
        }
    }

    @Test
    fun testCacheKey() {
        BitmapConfig.LOW_QUALITY.apply {
            Assert.assertEquals("BitmapConfig(LOW_QUALITY)", cacheKey)
        }
        BitmapConfig.MIDDEN_QUALITY.apply {
            Assert.assertEquals("BitmapConfig(MIDDEN_QUALITY)", cacheKey)
        }
        BitmapConfig.HIGH_QUALITY.apply {
            Assert.assertEquals("BitmapConfig(HIGH_QUALITY)", cacheKey)
        }
        BitmapConfig(RGB_565).apply {
            Assert.assertEquals("BitmapConfig(RGB_565)", cacheKey)
        }
    }

    @Test
    fun testGetConfigByMimeType() {
        BitmapConfig.LOW_QUALITY.apply {
            Assert.assertEquals(RGB_565, getConfigByMimeType("image/jpeg"))
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                Assert.assertEquals(ARGB_4444, getConfigByMimeType("image/png"))
            } else {
                Assert.assertEquals(ARGB_8888, getConfigByMimeType("image/png"))
            }
            Assert.assertEquals(ARGB_8888, getConfigByMimeType(null))
        }

        BitmapConfig.MIDDEN_QUALITY.apply {
            Assert.assertEquals(ARGB_8888, getConfigByMimeType("image/jpeg"))
            Assert.assertEquals(ARGB_8888, getConfigByMimeType("image/png"))
            Assert.assertEquals(ARGB_8888, getConfigByMimeType(null))
        }

        BitmapConfig.HIGH_QUALITY.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Assert.assertEquals(RGBA_F16, getConfigByMimeType("image/jpeg"))
                Assert.assertEquals(RGBA_F16, getConfigByMimeType("image/png"))
                Assert.assertEquals(RGBA_F16, getConfigByMimeType(null))
            } else {
                Assert.assertEquals(ARGB_8888, getConfigByMimeType("image/jpeg"))
                Assert.assertEquals(ARGB_8888, getConfigByMimeType("image/png"))
                Assert.assertEquals(ARGB_8888, getConfigByMimeType(null))
            }
        }

        BitmapConfig(RGB_565).apply {
            Assert.assertEquals(RGB_565, getConfigByMimeType("image/jpeg"))
            Assert.assertEquals(RGB_565, getConfigByMimeType("image/png"))
            Assert.assertEquals(RGB_565, getConfigByMimeType(null))
        }
    }
}