package com.github.panpf.sketch.test.fetch

import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DataFrom
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FetchResultTest {

    @Test
    fun testFrom() {
        FetchResult(FileDataSource(File("/sdcard/sample.jpeg"))).apply {
            Assert.assertEquals(DataFrom.LOCAL, from)
        }

        FetchResult(ByteArrayDataSource(byteArrayOf(), DataFrom.NETWORK)).apply {
            Assert.assertEquals(DataFrom.NETWORK, from)
        }
    }

    @Test
    fun testToString() {
        FetchResult(FileDataSource(File("/sdcard/sample.jpeg"))).apply {
            Assert.assertEquals("FetchResult(source=FileDataSource(from=LOCAL, file=/sdcard/sample.jpeg))", this.toString())
        }

        FetchResult(ByteArrayDataSource(byteArrayOf(), DataFrom.NETWORK)).apply {
            Assert.assertEquals("FetchResult(source=ByteArrayDataSource(from=NETWORK, length=0))", this.toString())
        }
    }
}