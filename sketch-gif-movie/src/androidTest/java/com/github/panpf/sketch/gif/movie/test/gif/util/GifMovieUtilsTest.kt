package com.github.panpf.sketch.gif.movie.test.gif.util

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.gif.util.allocationByteCountCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifMovieUtilsTest {

    @Test
    fun testAllocationByteCountCompat() {
        Assert.assertEquals(
            110 * 210 * 4,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).allocationByteCountCompat
        )

        Assert.assertEquals(
            110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).allocationByteCountCompat
        )

        Assert.assertEquals(
            0,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565)
                .apply { recycle() }
                .allocationByteCountCompat
        )
    }
}