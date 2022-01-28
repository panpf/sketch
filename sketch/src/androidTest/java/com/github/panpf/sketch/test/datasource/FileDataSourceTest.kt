package com.github.panpf.sketch.test.datasource

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class FileDataSourceTest {

    @Test
    fun testConstructor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val file = runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("sample.jpeg")),
                assetFileName = "sample.jpeg"
            ).file()
        }
        val request = LoadRequest(newFileUri(file.path))
        FileDataSource(
            sketch = sketch,
            request = request,
            file = file
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertTrue(file === this.file)
            Assert.assertEquals(DataFrom.LOCAL, this.from)
        }
    }

    @Test
    fun testLength() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val file = runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("sample.jpeg")),
                assetFileName = "sample.jpeg"
            ).file()
        }
        FileDataSource(
            sketch = sketch,
            request = LoadRequest(newFileUri(file.path)),
            file = file
        ).apply {
            Assert.assertEquals(540456, length())
        }

        FileDataSource(
            sketch = sketch,
            request = LoadRequest(newFileUri("/sdcard/not_found.jpeg")),
            file = File("/sdcard/not_found.jpeg")
        ).apply {
            Assert.assertEquals(0, length())
        }
    }

    @Test
    fun testNewFileDescriptor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val file = runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("sample.jpeg")),
                assetFileName = "sample.jpeg"
            ).file()
        }
        FileDataSource(
            sketch = sketch,
            request = LoadRequest(newFileUri(file.path)),
            file = file
        ).apply {
            Assert.assertNull(newFileDescriptor())
        }

        FileDataSource(
            sketch = sketch,
            request = LoadRequest(newFileUri("/sdcard/not_found.jpeg")),
            file = File("/sdcard/not_found.jpeg")
        ).apply {
            Assert.assertNull(newFileDescriptor())
        }
    }

    @Test
    fun testNewInputStream() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val file = runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("sample.jpeg")),
                assetFileName = "sample.jpeg"
            ).file()
        }
        FileDataSource(
            sketch = sketch,
            request = LoadRequest(newFileUri(file.path)),
            file = file
        ).apply {
            newInputStream().close()
        }

        assertThrow(FileNotFoundException::class) {
            FileDataSource(
                sketch = sketch,
                request = LoadRequest(newFileUri("/sdcard/not_found.jpeg")),
                file = File("/sdcard/not_found.jpeg")
            ).apply {
                newInputStream()
            }
        }
    }

    @Test
    fun testFile() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val file = runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("sample.jpeg")),
                assetFileName = "sample.jpeg"
            ).file()
        }
        FileDataSource(
            sketch = sketch,
            request = LoadRequest(newFileUri(file.path)),
            file = file,
        ).apply {
            val file1 = runBlocking {
                file()
            }
            Assert.assertEquals(file, file1)
        }
    }

    @Test
    fun testToString() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val file = runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = LoadRequest(newAssetUri("sample.jpeg")),
                assetFileName = "sample.jpeg"
            ).file()
        }
        FileDataSource(
            sketch = sketch,
            request = LoadRequest(newFileUri(file.path)),
            file = file
        ).apply {
            Assert.assertEquals(
                "FileDataSource(file='${file.path}')",
                toString()
            )
        }

        FileDataSource(
            sketch = sketch,
            request = LoadRequest(newFileUri("/sdcard/not_found.jpeg")),
            file = File("/sdcard/not_found.jpeg")
        ).apply {
            Assert.assertEquals("FileDataSource(file='/sdcard/not_found.jpeg')", toString())
        }
    }
}