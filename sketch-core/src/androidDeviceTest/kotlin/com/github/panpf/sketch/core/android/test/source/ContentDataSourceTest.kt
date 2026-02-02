package com.github.panpf.sketch.core.android.test.source

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import kotlinx.coroutines.test.runTest
import okio.Closeable
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
class ContentDataSourceTest {

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val contentUri = ComposeResImageFiles.jpeg
            .toDataSource(context)
            .getFile(sketch)
            .let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            context = sketch.context,
            contentUri = contentUri,
        ).apply {
            assertEquals(contentUri, this.contentUri)
            assertEquals(LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testKey() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val contentUri = ComposeResImageFiles.jpeg
            .toDataSource(context)
            .getFile(sketch)
            .let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            context = sketch.context,
            contentUri = contentUri,
        ).apply {
            assertEquals(
                expected = contentUri.toString(),
                actual = key
            )
        }
    }

    @Test
    fun testOpenSource() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val contentUri = ComposeResImageFiles.jpeg
            .toDataSource(context)
            .getFile(sketch)
            .let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            context = sketch.context,
            contentUri = contentUri,
        ).openSource().asOrThrow<Closeable>().close()

        assertFailsWith(FileNotFoundException::class) {
            val errorContentUri = Uri.fromFile(File("/sdcard/error.jpeg"))
            ContentDataSource(
                context = sketch.context,
                contentUri = errorContentUri,
            ).openSource()
        }
    }

    @Test
    fun testGetFile() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val contentUri = ComposeResImageFiles.jpeg
            .toDataSource(context)
            .getFile(sketch)
            .let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            context = sketch.context,
            contentUri = contentUri,
        ).apply {
            val file = getFile(sketch)
            assertEquals("6a473c2d8abe13e707f18e812e00229c.0", file.name)
        }

        val errorContentUri = Uri.fromFile(File("/sdcard/error.jpeg"))
        ContentDataSource(
            context = sketch.context,
            contentUri = errorContentUri,
        ).apply {
            val file = getFile(sketch)
            assertEquals("/sdcard/error.jpeg", file.toFile().path)
        }

        assertFailsWith(IOException::class) {
            val errorContentUri1 = Uri.parse("content://fake/fake.jpeg")
            ContentDataSource(
                context = sketch.context,
                contentUri = errorContentUri1,
            ).getFile(sketch)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val contentUri = ComposeResImageFiles.jpeg
            .toDataSource(context)
            .getFile(sketch)
            .let { Uri.fromFile(it.toFile()) }
        val element1 = ContentDataSource(
            context = sketch.context,
            contentUri = contentUri,
        )
        val element11 = ContentDataSource(
            context = sketch.context,
            contentUri = contentUri,
        )

        val errorContentUri = Uri.fromFile(File("/sdcard/error.jpeg"))
        val element2 = ContentDataSource(
            context = sketch.context,
            contentUri = errorContentUri,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val contentUri = ComposeResImageFiles.jpeg
            .toDataSource(context)
            .getFile(sketch)
            .let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            context = sketch.context,
            contentUri = contentUri,
        ).apply {
            assertEquals(
                "ContentDataSource('$contentUri')",
                toString()
            )
        }

        val errorContentUri = Uri.fromFile(File("/sdcard/error.jpeg"))
        ContentDataSource(
            context = sketch.context,
            contentUri = errorContentUri,
        ).apply {
            assertEquals(
                "ContentDataSource('file:///sdcard/error.jpeg')",
                toString()
            )
        }
    }
}