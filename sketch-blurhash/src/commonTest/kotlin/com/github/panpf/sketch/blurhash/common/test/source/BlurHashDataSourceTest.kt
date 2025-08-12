package com.github.panpf.sketch.blurhash.common.test.source

import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class BlurHashDataSourceTest {

    private val blurHash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"
    private val blurHash2 = "LKN]Rv%2Tw=w]~RBVZRi};RPxuwH"

    @Test
    fun testConstructor() {
        BlurHashDataSource(blurHash, DataFrom.LOCAL)
    }

    @Test
    fun testKey() {
        BlurHashDataSource(blurHash, DataFrom.LOCAL).apply {
            assertEquals(expected = blurHash, actual = key)
        }
    }

    @Test
    fun testOpenSource() {
        assertFailsWith(UnsupportedOperationException::class) {
            BlurHashDataSource(blurHash, DataFrom.LOCAL).openSource()
        }
    }

    @Test
    fun testGetFile() {
        val (_, sketch) = getTestContextAndSketch()
        assertFailsWith(UnsupportedOperationException::class) {
            BlurHashDataSource(blurHash, DataFrom.LOCAL).getFile(sketch)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurHashDataSource(blurHash, DataFrom.LOCAL)
        val element11 = BlurHashDataSource(blurHash, DataFrom.LOCAL)
        val element2 = BlurHashDataSource(blurHash2, DataFrom.LOCAL)
        val element3 = BlurHashDataSource(blurHash, DataFrom.NETWORK)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "BlurHashDataSource(blurHash='$blurHash', dataFrom=LOCAL)",
            actual = BlurHashDataSource(blurHash, DataFrom.LOCAL).toString()
        )
    }
}