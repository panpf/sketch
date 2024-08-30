package com.github.panpf.sketch.core.common.test.source

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.asOrThrow
import okio.Closeable
import okio.FileNotFoundException
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FileDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndSketch()
        val file = ResourceImages.jpeg.toDataSource(context).getFile(sketch)
        FileDataSource(path = file).apply {
            assertEquals(file, this.getFile(sketch))
            assertEquals(LOCAL, this.dataFrom)
        }
    }

    // TODO test: key

    @Test
    fun testNewInputStream() {
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
    fun testFile() {
        val (context, sketch) = getTestContextAndSketch()
        val file = ResourceImages.jpeg.toDataSource(context).getFile(sketch)
        FileDataSource(path = file).apply {
            val file1 = getFile(sketch)
            assertEquals(file, file1)
        }
    }

    // TODO equals and hashCode

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