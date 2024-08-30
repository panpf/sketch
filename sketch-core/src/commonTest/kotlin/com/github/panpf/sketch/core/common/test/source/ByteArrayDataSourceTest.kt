package com.github.panpf.sketch.core.common.test.source

import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.util.asOrThrow
import okio.Closeable
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteArrayDataSourceTest {

    @Test
    fun testConstructor() {
        ByteArrayDataSource(
            dataFrom = MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".encodeToByteArray()
        ).apply {
            assertEquals(MEMORY, this.dataFrom)
        }
    }

    // TODO test: key

    @Test
    fun testNewInputStream() {
        ByteArrayDataSource(
            dataFrom = MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".encodeToByteArray()
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }
    }

    // TODO equals and hashCode

    @Test
    fun testToString() {
        val data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".encodeToByteArray()
        ByteArrayDataSource(
            dataFrom = MEMORY,
            data = data
        ).apply {
            assertEquals(
                "ByteArrayDataSource(data=${data}, from=MEMORY)",
                toString()
            )
        }
    }
}