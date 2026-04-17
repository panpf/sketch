package com.github.panpf.sketch.core.desktop.test.source

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.fetch.newKotlinResourceUri
import com.github.panpf.sketch.images.KotlinResImageFiles
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.KotlinResourceDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlinx.coroutines.test.runTest
import okio.Path
import okio.buffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class KotlinResourceDataSourceTest {

    @Test
    fun testConstructor() {
        KotlinResourceDataSource("sample.jpeg").apply {
            assertEquals("sample.jpeg", resourcePath)
        }
        KotlinResourceDataSource("sample.png").apply {
            assertEquals("sample.png", resourcePath)
        }
    }

    @Test
    fun testKey() = runTest {
        KotlinResourceDataSource("sample.jpeg").apply {
            assertEquals(newKotlinResourceUri("sample.jpeg"), key)
        }
        KotlinResourceDataSource("sample.png").apply {
            assertEquals(newKotlinResourceUri("sample.png"), key)
        }
    }

    @Test
    fun testDataFrom() = runTest {
        KotlinResourceDataSource("sample.jpeg").apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }

    @Test
    fun testOpenSource() {
        KotlinResourceDataSource(KotlinResImageFiles.bear.name)
            .openSource().buffer()
            .use { it.readByteArray() }
    }

    @Test
    fun testGetFile() {
        val (_, sketch) = getTestContextAndSketch()
        val path1: Path
        KotlinResourceDataSource(KotlinResImageFiles.bear.name)
            .getFile(sketch).apply {
                path1 = this
                assertTrue(actual = toString().contains("${Path.DIRECTORY_SEPARATOR}${DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME}${Path.DIRECTORY_SEPARATOR}"))
            }

        val path2: Path
        KotlinResourceDataSource(KotlinResImageFiles.bear.name)
            .getFile(sketch).apply {
                path2 = this
                assertTrue(actual = toString().contains("${Path.DIRECTORY_SEPARATOR}${DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME}${Path.DIRECTORY_SEPARATOR}"))
            }

        assertEquals(expected = path1, actual = path2)
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val source1 = KotlinResourceDataSource("sample.jpeg")
        val source12 = KotlinResourceDataSource("sample.jpeg")
        val source2 = KotlinResourceDataSource("sample.png")

        assertEquals(expected = source1, actual = source1)
        assertEquals(expected = source1, actual = source12)
        assertNotEquals(illegal = source1, actual = source2)
        assertNotEquals(illegal = source1, actual = null as Any?)
        assertNotEquals(illegal = source1, actual = Any())

        assertEquals(expected = source1.hashCode(), actual = source12.hashCode())
        assertNotEquals(illegal = source1.hashCode(), actual = source2.hashCode())
    }

    @Test
    fun testToString() = runTest {
        assertEquals(
            expected = "KotlinResourceDataSource('sample.jpeg')",
            actual = KotlinResourceDataSource("sample.jpeg").toString()
        )
        assertEquals(
            expected = "KotlinResourceDataSource('sample.png')",
            actual = KotlinResourceDataSource("sample.png").toString()
        )
    }
}