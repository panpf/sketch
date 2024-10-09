package com.github.panpf.sketch.compose.core.common.test.resize

import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.resize.AsyncImageSizeResolver
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AsyncImageSizeResolverTest {

    @Test
    fun testSize() = runTest {
        val sizeResolver = AsyncImageSizeResolver(null)

        var resultSize: Size? = null
        val job = async(ioCoroutineDispatcher()) {
            resultSize = sizeResolver.size()
        }

        assertEquals(null, sizeResolver.sizeState.value)
        assertEquals(null, resultSize)

        block(100)
        assertEquals(null, sizeResolver.sizeState.value)
        assertEquals(null, resultSize)
        assertTrue(job.isActive)

        sizeResolver.sizeState.value = IntSize(101, 202)
        block(100)
        assertFalse(job.isActive)
        assertEquals(IntSize(101, 202), sizeResolver.sizeState.value)
        assertEquals(Size(101, 202), resultSize)

        job.await()
    }

    @Test
    fun testKey() {
        val sizeResolver = AsyncImageSizeResolver(null)
        assertEquals("AsyncImageSize", sizeResolver.key)
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = AsyncImageSizeResolver(null)
        val element11 = AsyncImageSizeResolver(null)
        val element2 = AsyncImageSizeResolver(IntSize(101, 202))

        assertNotEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val sizeResolver = AsyncImageSizeResolver(null)
        assertEquals("AsyncImageSizeResolver(size=null)", sizeResolver.toString())

        val sizeResolver2 = AsyncImageSizeResolver(IntSize(101, 202))
        assertEquals("AsyncImageSizeResolver(size=101x202)", sizeResolver2.toString())
    }
}