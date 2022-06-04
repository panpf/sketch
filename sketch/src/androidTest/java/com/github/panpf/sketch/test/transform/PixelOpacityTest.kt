package com.github.panpf.sketch.test.transform

import android.graphics.PixelFormat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.transform.PixelOpacity
import com.github.panpf.sketch.transform.flag
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PixelOpacityTest {

    @Test
    fun testFlag() {
        Assert.assertEquals(PixelFormat.OPAQUE, PixelOpacity.OPAQUE.flag)
        Assert.assertEquals(PixelFormat.UNKNOWN, PixelOpacity.UNCHANGED.flag)
        Assert.assertEquals(PixelFormat.TRANSLUCENT, PixelOpacity.TRANSLUCENT.flag)
    }
}