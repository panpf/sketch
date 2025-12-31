package com.github.panpf.sketch.core.common.test.source

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.asOrThrow
import okio.ByteString.Companion.toByteString
import okio.Closeable
import okio.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

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

    @Test
    fun testKey() {
        val data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".encodeToByteArray()
        ByteArrayDataSource(
            dataFrom = MEMORY,
            data = data
        ).apply {
            assertEquals(
                expected = data.toByteString().md5().hex(),
                actual = key
            )
        }
    }

    @Test
    fun testOpenSource() {
        ByteArrayDataSource(
            dataFrom = MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".encodeToByteArray()
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }
    }

    @Test
    fun testGetFile() {
        val (_, sketch) = getTestContextAndSketch()
        val data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".encodeToByteArray()
        ByteArrayDataSource(
            data = data,
            dataFrom = MEMORY,
        ).getFile(sketch).apply {
            assertTrue(actual = toString().contains("${Path.DIRECTORY_SEPARATOR}${DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME}${Path.DIRECTORY_SEPARATOR}"))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".encodeToByteArray()
        val data1 = "fd5717876ab046b8aa889c9aaac4b56c8j5f32".encodeToByteArray()
        val element1 = ByteArrayDataSource(
            data = data,
            dataFrom = MEMORY,
        )
        val element11 = ByteArrayDataSource(
            data = data,
            dataFrom = MEMORY,
        )
        val element2 = ByteArrayDataSource(
            data = data1,
            dataFrom = MEMORY,
        )
        val element3 = ByteArrayDataSource(
            data = data,
            dataFrom = LOCAL,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        val data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".encodeToByteArray()
        ByteArrayDataSource(
            data = data,
            dataFrom = MEMORY,
        ).apply {
            assertEquals(
                "ByteArrayDataSource(data=${data}, from=MEMORY)",
                toString()
            )
        }
    }
}