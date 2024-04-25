package com.github.panpf.sketch.core.android.test.source

import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.FileDataSource
import java.io.File
import java.io.FileNotFoundException

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
        val request = ImageRequest(context, newFileUri(file.path))
        FileDataSource(
            sketch = sketch,
            request = request,
            file = file
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
            request = ImageRequest(context, newFileUri(file.path)),
            file = file
        ).apply {
            openSource().close()
        }

        assertThrow(FileNotFoundException::class) {
            FileDataSource(
                sketch = sketch,
                request = ImageRequest(context, newFileUri("/sdcard/not_found.jpeg")),
                file = File("/sdcard/not_found.jpeg")
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
            request = ImageRequest(context, newFileUri(file.path)),
            file = file,
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
            request = ImageRequest(context, newFileUri(file.path)),
            file = file
        ).apply {
            Assert.assertEquals(
                "FileDataSource('${file.path}')",
                toString()
            )
        }

        FileDataSource(
            sketch = sketch,
            request = ImageRequest(context, newFileUri("/sdcard/not_found.jpeg")),
            file = File("/sdcard/not_found.jpeg")
        ).apply {
            Assert.assertEquals("FileDataSource('/sdcard/not_found.jpeg')", toString())
        }
    }
}