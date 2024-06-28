package com.github.panpf.sketch.core.android.test.source

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
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
        val (context, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, ResourceImages.jpeg.uri),
            assetFileName = ResourceImages.jpeg.resourceName
        ).getFile().let { Uri.fromFile(it.toFile()) }
        val request = ImageRequest(context, contentUri.toString())
        ContentDataSource(
            sketch = sketch,
            request = request,
            contentUri = contentUri,
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals(contentUri, this.contentUri)
            Assert.assertEquals(LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, ResourceImages.jpeg.uri),
            assetFileName = ResourceImages.jpeg.resourceName
        ).getFile().let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            sketch = sketch,
            request = ImageRequest(context, contentUri.toString()),
            contentUri = contentUri,
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }

        assertThrow(FileNotFoundException::class) {
            val errorContentUri = runBlocking {
                Uri.fromFile(File("/sdcard/error.jpeg"))
            }
            ContentDataSource(
                sketch = sketch,
                request = ImageRequest(context, errorContentUri.toString()),
                contentUri = errorContentUri,
            ).apply {
                openSource()
            }
        }
    }

    @Test
    fun testFile() {
        val (context, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, ResourceImages.jpeg.uri),
            assetFileName = ResourceImages.jpeg.resourceName
        ).getFile().let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            sketch = sketch,
            request = ImageRequest(context, contentUri.toString()),
            contentUri = contentUri,
        ).apply {
            val file = getFile()
            Assert.assertEquals("01d95711e2e30d06b88b93f82e3e1bde.0", file.name)
        }

        val errorContentUri = Uri.fromFile(File("/sdcard/error.jpeg"))
        ContentDataSource(
            sketch = sketch,
            request = ImageRequest(context, errorContentUri.toString()),
            contentUri = errorContentUri,
        ).apply {
            val file = getFile()
            Assert.assertEquals("/sdcard/error.jpeg", file.toFile().path)
        }

        assertThrow(FileNotFoundException::class) {
            val errorContentUri1 = Uri.parse("content://fake/fake.jpeg")
            ContentDataSource(
                sketch = sketch,
                request = ImageRequest(context, errorContentUri1.toString()),
                contentUri = errorContentUri1,
            ).getFile()
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndSketch()
        val contentUri = AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, ResourceImages.jpeg.uri),
            assetFileName = ResourceImages.jpeg.resourceName
        ).getFile().let { Uri.fromFile(it.toFile()) }
        ContentDataSource(
            sketch = sketch,
            request = ImageRequest(context, contentUri.toString()),
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
            sketch = sketch,
            request = ImageRequest(context, errorContentUri.toString()),
            contentUri = errorContentUri,
        ).apply {
            Assert.assertEquals(
                "ContentDataSource('file:///sdcard/error.jpeg')",
                toString()
            )
        }
    }
}