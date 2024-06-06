package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapsTest {

    @Test
    fun testAllocationByteCount() {
        Assert.assertEquals(
            110 * 210 * 4,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).allocationByteCount
        )

        Assert.assertEquals(
            110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).allocationByteCount
        )

        Assert.assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.O) 0 else 110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565)
                .apply { recycle() }
                .allocationByteCount
        )
    }

    @Test
    fun testByteCount() {
        Assert.assertEquals(
            110 * 210 * 4,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).byteCount
        )

        Assert.assertEquals(
            110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).byteCount
        )

        Assert.assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.O) 0 else 110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565)
                .apply { recycle() }
                .byteCount
        )
    }
}
