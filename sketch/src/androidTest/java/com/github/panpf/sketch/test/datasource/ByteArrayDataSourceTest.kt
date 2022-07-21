package com.github.panpf.sketch.test.datasource

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ByteArrayDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, "http://sample.jpeg")
        ByteArrayDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals(37, this.length())
            Assert.assertEquals(DataFrom.MEMORY, this.dataFrom)
        }
    }

    @Test
    fun testLength() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, "http://sample.jpeg")

        ByteArrayDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            Assert.assertEquals(37, length())
            Assert.assertEquals(37, length())
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndNewSketch()
        ByteArrayDataSource(
            sketch = sketch,
            request = LoadRequest(context, "http://sample.jpeg"),
            dataFrom = DataFrom.MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            newInputStream().close()
        }
    }

    @Test
    fun testFile() {
        val (context, sketch) = getTestContextAndNewSketch()
        ByteArrayDataSource(
            sketch = sketch,
            request = LoadRequest(context, "http://sample.jpeg"),
            dataFrom = DataFrom.MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            val file = runBlocking { file() }
            Assert.assertEquals("369c0aa172a8ac158a372f9b00fbd220.0", file.name)
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()
        ByteArrayDataSource(
            sketch = sketch,
            request = LoadRequest(context, "http://sample.jpeg"),
            dataFrom = DataFrom.MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            Assert.assertEquals(
                "ByteArrayDataSource(from=MEMORY,length=37)",
                toString()
            )
        }
    }
}