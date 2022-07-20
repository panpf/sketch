package com.github.panpf.sketch.test.request

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.transform.createCircleCropTransformed
import com.github.panpf.sketch.util.UnknownException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayResultTest {

    @Test
    fun test() {
        val context = getTestContext()
        val request1 = DisplayRequest(context, "http://sample.com/sample.jpeg")

        DisplayResult.Success(
            request1,
            ColorDrawable(Color.BLACK),
            ImageInfo(100, 100, "image/jpeg", ExifInterface.ORIENTATION_ROTATE_90),
            LOCAL,
            listOf(createCircleCropTransformed(END_CROP))
        ).apply {
            Assert.assertSame(request1, request)
            Assert.assertTrue(drawable is ColorDrawable)
            Assert.assertEquals(ImageInfo(100, 100, "image/jpeg", ExifInterface.ORIENTATION_ROTATE_90), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(listOf(createCircleCropTransformed(END_CROP)), transformedList)
        }

        DisplayResult.Error(request1, ColorDrawable(Color.BLACK), UnknownException("")).apply {
            Assert.assertSame(request1, request)
            Assert.assertTrue(drawable is ColorDrawable)
            Assert.assertTrue(exception is UnknownException)
        }
    }
}