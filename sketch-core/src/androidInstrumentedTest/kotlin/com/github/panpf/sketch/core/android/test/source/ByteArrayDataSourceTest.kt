package com.github.panpf.sketch.core.android.test.source

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import okio.Closeable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ByteArrayDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.jpeg")
        ByteArrayDataSource(
            sketch = sketch,
            request = request,
            dataFrom = MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals(MEMORY, this.dataFrom)
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndSketch()
        ByteArrayDataSource(
            sketch = sketch,
            request = ImageRequest(context, "http://sample.jpeg"),
            dataFrom = MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }
    }

    // TODO equals and hashCode

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndSketch()
        val data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ByteArrayDataSource(
            sketch = sketch,
            request = ImageRequest(context, "http://sample.jpeg"),
            dataFrom = MEMORY,
            data = data
        ).apply {
            Assert.assertEquals(
                "ByteArrayDataSource(data=${data}, from=MEMORY)",
                toString()
            )
        }
    }
}