package com.github.panpf.sketch.test.decode

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.decode.transform.RotateTransformed
import com.github.panpf.sketch.request.DataFrom.LOCAL
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawableDecodeResultTest {

    @Test
    fun testConstructor() {
        val newDrawable = ColorDrawable(Color.RED)
        val imageInfo = ImageInfo("image/png", 3000, 500, 0)
        val transformedList = listOf(InSampledTransformed(4), RotateTransformed(45))
        DrawableDecodeResult(newDrawable, imageInfo, LOCAL, transformedList).apply {
            Assert.assertTrue(newDrawable === drawable)
            Assert.assertEquals(
                "ImageInfo(mimeType='image/png',width=3000,height=500,exifOrientation=UNDEFINED)",
                imageInfo.toString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(
                "InSampledTransformed(4), RotateTransformed(45)",
                this.transformedList?.joinToString()
            )
        }
    }
}