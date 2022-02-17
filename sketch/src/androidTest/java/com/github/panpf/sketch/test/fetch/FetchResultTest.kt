package com.github.panpf.sketch.test.fetch

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FetchResultTest {

    @Test
    fun testFrom() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest("")

        FetchResult(FileDataSource(sketch, request, File("/sdcard/sample.jpeg")), "image/jpeg").apply {
            Assert.assertEquals(DataFrom.LOCAL, from)
        }

        FetchResult(ByteArrayDataSource(sketch, request, DataFrom.NETWORK, byteArrayOf()), "image/jpeg").apply {
            Assert.assertEquals(DataFrom.NETWORK, from)
        }
    }

    @Test
    fun testToString() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest("")

        FetchResult(FileDataSource(sketch, request, File("/sdcard/sample.jpeg")), "image/jpeg").apply {
            Assert.assertEquals("FetchResult(source=FileDataSource(file='/sdcard/sample.jpeg'),mimeType='image/jpeg')", this.toString())
        }

        FetchResult(ByteArrayDataSource(sketch, request, DataFrom.NETWORK, byteArrayOf()), "image/jpeg").apply {
            Assert.assertEquals("FetchResult(source=ByteArrayDataSource(from=NETWORK,length=0),mimeType='image/jpeg')", this.toString())
        }
    }
}