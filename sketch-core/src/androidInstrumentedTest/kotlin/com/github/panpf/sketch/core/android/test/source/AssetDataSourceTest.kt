package com.github.panpf.sketch.core.android.test.source

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import okio.Closeable
import okio.Path
import org.junit.runner.RunWith
import java.io.FileNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

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

    @Test
    fun testKey() {
        val context = getTestContext()
        AssetDataSource(
            context = context,
            fileName = ResourceImages.jpeg.resourceName
        ).apply {
            assertEquals(
                expected = newAssetUri(fileName = ResourceImages.jpeg.resourceName),
                actual = key
            )
        }
    }

    @Test
    fun testOpenSource() {
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

    @Test
    fun testGetFile() {
        val (context, sketch) = getTestContextAndSketch()
        AssetDataSource(
            context = context,
            fileName = ResourceImages.jpeg.resourceName
        ).getFile(sketch).apply {
            assertTrue(actual = toString().contains("${Path.DIRECTORY_SEPARATOR}${DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME}${Path.DIRECTORY_SEPARATOR}"))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = AssetDataSource(
            context = context,
            fileName = ResourceImages.jpeg.resourceName
        )
        val element11 = AssetDataSource(
            context = context,
            fileName = ResourceImages.jpeg.resourceName
        )
        val element2 = AssetDataSource(
            context = context,
            fileName = ResourceImages.png.resourceName
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

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