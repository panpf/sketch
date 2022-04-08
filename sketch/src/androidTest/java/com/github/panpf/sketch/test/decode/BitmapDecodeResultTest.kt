package com.github.panpf.sketch.test.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.transform.CircleCropTransformed
import com.github.panpf.sketch.transform.RotateTransformed
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapDecodeResultTest {

    @Test
    fun testConstructor() {
        val newBitmap = Bitmap.createBitmap(100, 100, RGB_565)
        val imageInfo = ImageInfo(3000, 500, "image/png")
        val transformedList = listOf(InSampledTransformed(4), RotateTransformed(45))
        BitmapDecodeResult(newBitmap, imageInfo, 0, LOCAL, transformedList).apply {
            Assert.assertTrue(newBitmap === bitmap)
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

    @Test
    fun testNew() {
        val newBitmap = Bitmap.createBitmap(100, 100, RGB_565)
        val imageInfo = ImageInfo(3000, 500, "image/png")
        val transformedList = listOf(InSampledTransformed(4), RotateTransformed(45))
        val result = BitmapDecodeResult(newBitmap, imageInfo, 0, LOCAL, transformedList)
        Assert.assertEquals(
            "InSampledTransformed(4), RotateTransformed(45)",
            result.transformedList?.joinToString()
        )

        val result2 = result.newResult(newBitmap) {
            addTransformed(CircleCropTransformed(CENTER_CROP))
        }
        Assert.assertEquals(
            "InSampledTransformed(4), RotateTransformed(45)",
            result.transformedList?.joinToString()
        )
        Assert.assertEquals(
            "InSampledTransformed(4), RotateTransformed(45), CircleCropTransformed(CENTER_CROP)",
            result2.transformedList?.joinToString()
        )
    }
}