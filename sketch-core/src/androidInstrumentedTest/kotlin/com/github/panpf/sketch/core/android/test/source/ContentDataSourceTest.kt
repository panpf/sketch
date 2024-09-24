package com.github.panpf.sketch.core.android.test.source

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import okio.Closeable
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

@RunWith(AndroidJUnit4::class)
class ContentDataSourceTest {

    @Test
    fun testConstructor() {
        val (_, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            context = sketch.context,
            fileName = ResourceImages.jpeg.resourceName
        ).getFile(sketch).let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            context = sketch.context,
            contentUri = contentUri,
        ).apply {
            assertEquals(contentUri, this.contentUri)
            assertEquals(LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testKey() {
        val (_, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            context = sketch.context,
            fileName = ResourceImages.jpeg.resourceName
        ).getFile(sketch).let { Uri.fromFile(it.toFile()) }
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
    fun testOpenSource() {
        val (_, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            context = sketch.context,
            fileName = ResourceImages.jpeg.resourceName
        ).getFile(sketch).let { Uri.fromFile(it.toFile()) }
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
    fun testGetFile() {
        val (_, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            context = sketch.context,
            fileName = ResourceImages.jpeg.resourceName
        ).getFile(sketch).let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            context = sketch.context,
            contentUri = contentUri,
        ).apply {
            val file = getFile(sketch)
            assertEquals("4d0b3d81c4eacfc1252f7112ca8833b3.0", file.name)
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
    fun testEqualsAndHashCode() {
        val (_, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            context = sketch.context,
            fileName = ResourceImages.jpeg.resourceName
        ).getFile(sketch).let { Uri.fromFile(it.toFile()) }
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

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element2, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element2, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val (_, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            context = sketch.context,
            fileName = ResourceImages.jpeg.resourceName
        ).getFile(sketch).let { Uri.fromFile(it.toFile()) }
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