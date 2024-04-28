package com.github.panpf.sketch.core.android.test.source

import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
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

@RunWith(AndroidJUnit4::class)
class AssetDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndNewSketch()

        val request = ImageRequest(context, MyImages.jpeg.uri)
        AssetDataSource(
            sketch = sketch,
            request = request,
            assetFileName = MyImages.jpeg.fileName
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals(MyImages.jpeg.fileName, this.assetFileName)
            Assert.assertEquals(LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndNewSketch()

        AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, MyImages.jpeg.uri),
            assetFileName = MyImages.jpeg.fileName
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }

        assertThrow(FileNotFoundException::class) {
            AssetDataSource(
                sketch = sketch,
                request = ImageRequest(context, newAssetUri("not_found.jpeg")),
                assetFileName = "not_found.jpeg"
            ).apply {
                openSource()
            }
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()

        AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, MyImages.jpeg.uri),
            assetFileName = MyImages.jpeg.fileName
        ).apply {
            Assert.assertEquals(
                "AssetDataSource('sample.jpeg')",
                toString()
            )
        }

        AssetDataSource(
            sketch = sketch,
            request = ImageRequest(context, newAssetUri("not_found.jpeg")),
            assetFileName = "not_found.jpeg"
        ).apply {
            Assert.assertEquals("AssetDataSource('not_found.jpeg')", toString())
        }
    }
}