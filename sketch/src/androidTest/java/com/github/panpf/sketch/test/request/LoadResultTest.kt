package com.github.panpf.sketch.test.request

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.transform.CircleCropTransformed
import com.github.panpf.sketch.util.UnknownException
import com.github.panpf.sketch.util.toShortInfoString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadResultTest {

    @Test
    fun test() {
        val context = getTestContext()
        val request1 = LoadRequest(context, "http://sample.com/sample.jpeg")

        LoadResult.Success(
            request1,
            Bitmap.createBitmap(100, 100, RGB_565),
            ImageInfo(100, 100, "image/jpeg"),
            ExifInterface.ORIENTATION_ROTATE_90,
            LOCAL,
            listOf(CircleCropTransformed(END_CROP))
        ).apply {
            Assert.assertSame(request1, request)
            Assert.assertEquals("Bitmap(100x100,RGB_565)", bitmap.toShortInfoString())
            Assert.assertEquals(ImageInfo(100, 100, "image/jpeg"), imageInfo)
            Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_90, imageExifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(listOf(CircleCropTransformed(END_CROP)), transformedList)
        }

        LoadResult.Error(request1, UnknownException("")).apply {
            Assert.assertSame(request1, request)
            Assert.assertTrue(exception is UnknownException)
        }
    }
}