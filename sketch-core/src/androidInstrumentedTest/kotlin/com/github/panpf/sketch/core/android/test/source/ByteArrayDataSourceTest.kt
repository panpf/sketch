package com.github.panpf.sketch.core.android.test.source

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.test.utils.asOrThrow
import okio.Closeable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ByteArrayDataSourceTest {

    @Test
    fun testConstructor() {
        ByteArrayDataSource(
            dataFrom = MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            Assert.assertEquals(MEMORY, this.dataFrom)
        }
    }

    // TODO test: key

    @Test
    fun testNewInputStream() {
        ByteArrayDataSource(
            dataFrom = MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }
    }

    // TODO equals and hashCode

    @Test
    fun testToString() {
        val data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ByteArrayDataSource(
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