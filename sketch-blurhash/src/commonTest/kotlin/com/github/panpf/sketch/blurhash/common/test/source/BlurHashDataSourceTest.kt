package com.github.panpf.sketch.blurhash.common.test.source

import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.toUri
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class BlurHashDataSourceTest {

    private val blurHash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"
    private val blurHash2 = "LKN]Rv%2Tw=w]~RBVZRi};RPxuwH"

    @Test
    fun testConstructor() {
        BlurHashDataSource(newBlurHashUri(blurHash).toUri())
    }

    @Test
    fun testKey() {
        val blurHashUri = newBlurHashUri(blurHash)
        assertEquals(
            expected = blurHashUri,
            actual = BlurHashDataSource(blurHashUri.toUri()).key
        )
    }

    @Test
    fun testDataFrom() {
        val blurHashUri = newBlurHashUri(blurHash)
        assertEquals(
            expected = DataFrom.MEMORY,
            actual = BlurHashDataSource(blurHashUri.toUri()).dataFrom
        )
    }

    @Test
    fun testOpenSource() {
        assertFailsWith(UnsupportedOperationException::class) {
            BlurHashDataSource(newBlurHashUri(blurHash).toUri()).openSource()
        }
    }

    @Test
    fun testGetFile() {
        val (_, sketch) = getTestContextAndSketch()
        assertFailsWith(UnsupportedOperationException::class) {
            BlurHashDataSource(newBlurHashUri(blurHash).toUri()).getFile(sketch)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurHashDataSource(newBlurHashUri(blurHash).toUri())
        val element11 = BlurHashDataSource(newBlurHashUri(blurHash).toUri())
        val element2 = BlurHashDataSource(newBlurHashUri(blurHash2).toUri())

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val blurHashUri = newBlurHashUri(blurHash).toUri()
        assertEquals(
            expected = "BlurHashDataSource(blurHashUri='$blurHashUri')",
            actual = BlurHashDataSource(blurHashUri).toString()
        )
    }
}