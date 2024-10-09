package com.github.panpf.sketch.compose.core.common.test.resize

import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.resize.AsyncImageSizeResolver
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import com.github.panpf.sketch.util.toHexString
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
        val sizeResolver = AsyncImageSizeResolver()

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
        val sizeResolver = AsyncImageSizeResolver()
        assertEquals("AsyncImageSize", sizeResolver.key)
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = AsyncImageSizeResolver()
        val element11 = AsyncImageSizeResolver()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val sizeResolver = AsyncImageSizeResolver()
        assertEquals(
            expected = "AsyncImageSizeResolver@${sizeResolver.toHexString()}",
            actual = sizeResolver.toString()
        )
    }
}