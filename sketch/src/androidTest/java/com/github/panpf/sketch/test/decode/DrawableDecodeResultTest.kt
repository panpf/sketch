package com.github.panpf.sketch.test.decode

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.transform.RotateTransformed
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawableDecodeResultTest {

    @Test
    fun testConstructor() {
        val newDrawable = ColorDrawable(Color.RED)
        val imageInfo = ImageInfo(3000, 500, "image/png")
        val transformedList = listOf(InSampledTransformed(4), RotateTransformed(45))
        DrawableDecodeResult(newDrawable, imageInfo, 0, LOCAL, transformedList).apply {
            Assert.assertTrue(newDrawable === drawable)
            Assert.assertEquals(
                "ImageInfo(width=3000, height=500, mimeType='image/png')",
                imageInfo.toString()
            )
            Assert.assertEquals(0, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(
                "InSampledTransformed(4), RotateTransformed(45)",
                this.transformedList?.joinToString()
            )
        }
    }
}