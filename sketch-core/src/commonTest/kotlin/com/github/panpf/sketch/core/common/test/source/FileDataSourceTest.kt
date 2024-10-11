package com.github.panpf.sketch.core.common.test.source

import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.asOrThrow
import okio.Closeable
import okio.FileNotFoundException
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class FileDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndSketch()
        val file = ResourceImages.jpeg.toDataSource(context).getFile(sketch)
        FileDataSource(path = file).apply {
            assertEquals(file, this.getFile(sketch))
            assertEquals(DataFrom.LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testKey() {
        FileDataSource(
            path = "/sdcard/not_found.jpeg".toPath(),
            dataFrom = DataFrom.NETWORK
        ).apply {
            assertEquals(
                expected = newFileUri(path = path),
                actual = key
            )
        }
    }

    @Test
    fun testOpenSource() {
        val (context, sketch) = getTestContextAndSketch()
        val file = ResourceImages.jpeg.toDataSource(context).getFile(sketch)
        FileDataSource(path = file).apply {
            openSource().asOrThrow<Closeable>().close()
        }

        assertFailsWith(FileNotFoundException::class) {
            FileDataSource(path = "/sdcard/not_found.jpeg".toPath()).apply {
                openSource()
            }
        }
    }

    @Test
    fun testGetFile() {
        val (context, sketch) = getTestContextAndSketch()
        val file = ResourceImages.jpeg.toDataSource(context).getFile(sketch)
        FileDataSource(path = file).getFile(sketch).apply {
            assertEquals(file, this)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = FileDataSource(
            path = "/sdcard/not_found.jpeg".toPath(),
            dataFrom = DataFrom.NETWORK
        )
        val element11 = FileDataSource(
            path = "/sdcard/not_found.jpeg".toPath(),
            dataFrom = DataFrom.NETWORK
        )
        val element2 = FileDataSource(
            path = "/sdcard/not_found.png".toPath(),
            dataFrom = DataFrom.NETWORK
        )
        val element3 = FileDataSource(
            path = "/sdcard/not_found.jpeg".toPath(),
            dataFrom = DataFrom.LOCAL
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
        val (context, sketch) = getTestContextAndSketch()
        val file = ResourceImages.jpeg.toDataSource(context).getFile(sketch)
        FileDataSource(path = file).apply {
            assertEquals(
                "FileDataSource(path='${file}', from=LOCAL)",
                toString()
            )
        }

        FileDataSource(
            path = "/sdcard/not_found.jpeg".toPath(),
            dataFrom = DataFrom.NETWORK
        ).apply {
            assertEquals(
                "FileDataSource(path='/sdcard/not_found.jpeg', from=NETWORK)",
                toString()
            )
        }
    }
}