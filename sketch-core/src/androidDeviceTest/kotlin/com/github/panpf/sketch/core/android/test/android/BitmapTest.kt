package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class BitmapsTest {

    @Test
    fun testAllocationByteCount() {
        assertEquals(
            110 * 210 * 4,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).allocationByteCount
        )

        assertEquals(
            110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).allocationByteCount
        )

        assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.O) 0 else 110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565)
                .apply { recycle() }
                .allocationByteCount
        )
    }

    @Test
    fun testByteCount() {
        assertEquals(
            110 * 210 * 4,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).byteCount
        )

        assertEquals(
            110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).byteCount
        )

        assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.O) 0 else 110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565)
                .apply { recycle() }
                .byteCount
        )
    }
}
