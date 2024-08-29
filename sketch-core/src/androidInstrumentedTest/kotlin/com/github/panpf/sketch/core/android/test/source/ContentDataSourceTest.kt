package com.github.panpf.sketch.core.android.test.source

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import okio.Closeable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException

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
            Assert.assertEquals(contentUri, this.contentUri)
            Assert.assertEquals(LOCAL, this.dataFrom)
        }
    }

    // TODO test: key

    @Test
    fun testNewInputStream() {
        val (_, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            context = sketch.context,
            fileName = ResourceImages.jpeg.resourceName
        ).getFile(sketch).let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            context = sketch.context,
            contentUri = contentUri,
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }

        assertThrow(FileNotFoundException::class) {
            val errorContentUri = runBlocking {
                Uri.fromFile(File("/sdcard/error.jpeg"))
            }
            ContentDataSource(
                context = sketch.context,
                contentUri = errorContentUri,
            ).apply {
                openSource()
            }
        }
    }

    @Test
    fun testFile() {
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
            Assert.assertEquals("4d0b3d81c4eacfc1252f7112ca8833b3.0", file.name)
        }

        val errorContentUri = Uri.fromFile(File("/sdcard/error.jpeg"))
        ContentDataSource(
            context = sketch.context,
            contentUri = errorContentUri,
        ).apply {
            val file = getFile(sketch)
            Assert.assertEquals("/sdcard/error.jpeg", file.toFile().path)
        }

        assertThrow(FileNotFoundException::class) {
            val errorContentUri1 = Uri.parse("content://fake/fake.jpeg")
            ContentDataSource(
                context = sketch.context,
                contentUri = errorContentUri1,
            ).getFile(sketch)
        }
    }

    // TODO equals and hashCode

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
            Assert.assertEquals(
                "ContentDataSource('$contentUri')",
                toString()
            )
        }

        val errorContentUri = runBlocking {
            Uri.fromFile(File("/sdcard/error.jpeg"))
        }
        ContentDataSource(
            context = sketch.context,
            contentUri = errorContentUri,
        ).apply {
            Assert.assertEquals(
                "ContentDataSource('file:///sdcard/error.jpeg')",
                toString()
            )
        }
    }
}