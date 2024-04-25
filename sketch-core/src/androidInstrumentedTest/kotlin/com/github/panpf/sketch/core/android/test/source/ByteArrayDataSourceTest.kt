package com.github.panpf.sketch.core.android.test.source

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.MEMORY

@RunWith(AndroidJUnit4::class)
class ByteArrayDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndNewSketch()
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
        val (context, sketch) = getTestContextAndNewSketch()
        ByteArrayDataSource(
            sketch = sketch,
            request = ImageRequest(context, "http://sample.jpeg"),
            dataFrom = MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            openSource().close()
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()
        ByteArrayDataSource(
            sketch = sketch,
            request = ImageRequest(context, "http://sample.jpeg"),
            dataFrom = MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            Assert.assertEquals(
                "ByteArrayDataSource(from=MEMORY,length=37)",
                toString()
            )
        }
    }
}