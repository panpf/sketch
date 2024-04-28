package com.github.panpf.sketch.core.android.test.source

import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.FileDataSource
import java.io.File
import java.io.FileNotFoundException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import okio.Closeable
import okio.Path.Companion.toOkioPath

@RunWith(AndroidJUnit4::class)
class FileDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndNewSketch()
        val file = AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, MyImages.jpeg.uri),
            assetFileName = MyImages.jpeg.fileName
        ).getFile()
        val request = ImageRequest(context, newFileUri(file))
        FileDataSource(
            sketch = sketch,
            request = request,
            path = file
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertTrue(file === this.getFile())
            Assert.assertEquals(LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndNewSketch()
        val file = AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, MyImages.jpeg.uri),
            assetFileName = MyImages.jpeg.fileName
        ).getFile()
        FileDataSource(
            sketch = sketch,
            request = ImageRequest(context, newFileUri(file)),
            path = file
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }

        assertThrow(FileNotFoundException::class) {
            FileDataSource(
                sketch = sketch,
                request = ImageRequest(context, newFileUri("/sdcard/not_found.jpeg")),
                path = File("/sdcard/not_found.jpeg").toOkioPath()
            ).apply {
                openSource()
            }
        }
    }

    @Test
    fun testFile() {
        val (context, sketch) = getTestContextAndNewSketch()
        val file = AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, MyImages.jpeg.uri),
            assetFileName = MyImages.jpeg.fileName
        ).getFile()
        FileDataSource(
            sketch = sketch,
            request = ImageRequest(context, newFileUri(file)),
            path = file,
        ).apply {
            val file1 = getFile()
            Assert.assertEquals(file, file1)
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()
        val file = AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, MyImages.jpeg.uri),
            assetFileName = MyImages.jpeg.fileName
        ).getFile()
        FileDataSource(
            sketch = sketch,
            request = ImageRequest(context, newFileUri(file)),
            path = file
        ).apply {
            Assert.assertEquals(
                "FileDataSource('${file}')",
                toString()
            )
        }

        FileDataSource(
            sketch = sketch,
            request = ImageRequest(context, newFileUri("/sdcard/not_found.jpeg")),
            path = File("/sdcard/not_found.jpeg").toOkioPath()
        ).apply {
            Assert.assertEquals("FileDataSource('/sdcard/not_found.jpeg')", toString())
        }
    }
}