package com.github.panpf.sketch.core.android.test.source

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import okio.Closeable
import org.junit.runner.RunWith
import java.io.FileNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(AndroidJUnit4::class)
class AssetDataSourceTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()

        AssetDataSource(
            context = context,
            fileName = ResourceImages.jpeg.resourceName
        ).apply {
            assertEquals(ResourceImages.jpeg.resourceName, this.fileName)
            assertEquals(LOCAL, this.dataFrom)
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

        assertFailsWith(FileNotFoundException::class) {
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
            assertEquals(
                "AssetDataSource('sample.jpeg')",
                toString()
            )
        }

        AssetDataSource(
            context = context,
            fileName = "not_found.jpeg"
        ).apply {
            assertEquals("AssetDataSource('not_found.jpeg')", toString())
        }
    }
}