package com.github.panpf.sketch.core.android.test.source

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.tools4j.test.ktx.assertThrow
import okio.Closeable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class AssetDataSourceTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()

        AssetDataSource(
            context = context,
            fileName = ResourceImages.jpeg.resourceName
        ).apply {
            Assert.assertEquals(ResourceImages.jpeg.resourceName, this.fileName)
            Assert.assertEquals(LOCAL, this.dataFrom)
        }
    }

    // TODO test: key

    @Test
    fun testNewInputStream() {
        val context = getTestContext()

        AssetDataSource(
            context = context,
            fileName = ResourceImages.jpeg.resourceName
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }

        assertThrow(FileNotFoundException::class) {
            AssetDataSource(
                context = context,
                fileName = "not_found.jpeg"
            ).apply {
                openSource()
            }
        }
    }

    // TODO equals and hashCode

    @Test
    fun testToString() {
        val context = getTestContext()

        AssetDataSource(
            context = context,
            fileName = ResourceImages.jpeg.resourceName
        ).apply {
            Assert.assertEquals(
                "AssetDataSource('sample.jpeg')",
                toString()
            )
        }

        AssetDataSource(
            context = context,
            fileName = "not_found.jpeg"
        ).apply {
            Assert.assertEquals("AssetDataSource('not_found.jpeg')", toString())
        }
    }
}