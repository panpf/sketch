package com.github.panpf.sketch.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.UnknownException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadResultTest {

    @Test
    fun test() {
        val context = getTestContext()
        val request1 = DownloadRequest(context, "http://sample.com/sample.jpeg")

        DownloadResult.Success(request1, DownloadData(byteArrayOf(), MEMORY)).apply {
            Assert.assertSame(request1, request)
            Assert.assertNotNull(data)
            Assert.assertEquals(MEMORY, dataFrom)
        }

        DownloadResult.Error(request1, UnknownException("")).apply {
            Assert.assertSame(request1, request)
            Assert.assertTrue(exception is UnknownException)
        }
    }
}