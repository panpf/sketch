package com.github.panpf.sketch.test.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.SMPTE_C
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.DecodeConfig
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DecodeConfigTest {

    @Test
    fun testInSampleSize() {
        DecodeConfig().apply {
            Assert.assertNull(inSampleSize)

            inSampleSize = 4
            Assert.assertEquals(4, inSampleSize)
        }
    }

    @Test
    @Suppress("DEPRECATION")
    fun testInPreferQualityOverSpeed() {
        DecodeConfig().apply {
            Assert.assertNull(inPreferQualityOverSpeed)

            inPreferQualityOverSpeed = false
            Assert.assertEquals(false, inPreferQualityOverSpeed)
        }
    }

    @Test
    fun testInPreferredConfig() {
        DecodeConfig().apply {
            Assert.assertNull(inPreferredConfig)

            inPreferredConfig = RGB_565
            Assert.assertEquals(RGB_565, inPreferredConfig)
        }
    }

    @Test
    fun testInPreferredColorSpace() {
        DecodeConfig().apply {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                Assert.assertNull(inPreferredColorSpace)
            }

            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                inPreferredColorSpace = ColorSpace.get(SMPTE_C)
                Assert.assertEquals(ColorSpace.get(SMPTE_C), inPreferredColorSpace)
            }
        }
    }

    @Test
    fun testToBitmapOptions() {
        DecodeConfig().toBitmapOptions().apply {
            Assert.assertEquals(0, inSampleSize)
            Assert.assertEquals(false, inPreferQualityOverSpeed)
            Assert.assertEquals(Bitmap.Config.ARGB_8888, inPreferredConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                Assert.assertEquals(null, inPreferredColorSpace)
            }
        }

        DecodeConfig().apply {
            inSampleSize = 4
            inPreferQualityOverSpeed = true
            inPreferredConfig = RGB_565
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                inPreferredColorSpace = ColorSpace.get(SMPTE_C)
            }
        }.toBitmapOptions().apply {
            Assert.assertEquals(4, inSampleSize)
            if (VERSION.SDK_INT <= VERSION_CODES.M) {
                Assert.assertEquals(true, inPreferQualityOverSpeed)
            } else {
                Assert.assertEquals(false, inPreferQualityOverSpeed)
            }
            Assert.assertEquals(RGB_565, inPreferredConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                Assert.assertEquals(ColorSpace.get(SMPTE_C), inPreferredColorSpace)
            }
        }
    }
}