package com.github.panpf.sketch.core.common.test.cache

import com.github.panpf.sketch.cache.ImageCacheValue
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.defaultMemoryCacheSize
import com.github.panpf.sketch.cache.getExtras
import com.github.panpf.sketch.cache.getImageInfo
import com.github.panpf.sketch.cache.getResize
import com.github.panpf.sketch.cache.getTransformeds
import com.github.panpf.sketch.cache.newCacheValueExtras
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.transform.createRotateTransformed
import com.github.panpf.sketch.util.maxMemory
import kotlin.math.roundToLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MemoryCacheTest {

    @Test
    fun testBuilder() {
        val context = getTestContext()
        MemoryCache.Builder(context).build().apply {
            assertEquals(
                expected = context.defaultMemoryCacheSize(),
                actual = maxSize
            )
        }

        assertFailsWith(IllegalArgumentException::class) {
            MemoryCache.Builder(context).maxSizeBytes(0L)
        }
        assertFailsWith(IllegalArgumentException::class) {
            MemoryCache.Builder(context).maxSizeBytes(-1L)
        }
        MemoryCache.Builder(context).apply {
            maxSizeBytes(111L * 1024 * 1024)
        }.build().apply {
            assertEquals(
                expected = 111L * 1024 * 1024,
                actual = maxSize
            )
        }

        assertFailsWith(IllegalArgumentException::class) {
            MemoryCache.Builder(context).maxSizePercent(0.01)
        }
        assertFailsWith(IllegalArgumentException::class) {
            MemoryCache.Builder(context).maxSizePercent(1.01)
        }
        MemoryCache.Builder(context).apply {
            maxSizePercent(0.9)
        }.build().apply {
            assertEquals(
                expected = (0.9 * context.maxMemory()).roundToLong(),
                actual = maxSize
            )
        }
    }

    @Test
    fun testGetImageInfo() {
        ImageCacheValue(image = FakeImage(100, 100)).apply {
            assertEquals(
                expected = null,
                actual = getImageInfo()
            )
        }

        ImageCacheValue(
            image = FakeImage(100, 100), extras = newCacheValueExtras(
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                resize = Resize(200, 200),
                transformeds = null,
                extras = null
            )
        ).apply {
            assertEquals(
                expected = ImageInfo(100, 100, "image/jpeg"),
                actual = getImageInfo()
            )
        }
    }

    @Test
    fun testGetResize() {
        ImageCacheValue(image = FakeImage(100, 100)).apply {
            assertEquals(
                expected = null,
                actual = getResize()
            )
        }

        ImageCacheValue(
            image = FakeImage(100, 100), extras = newCacheValueExtras(
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                resize = Resize(200, 200),
                transformeds = null,
                extras = null
            )
        ).apply {
            assertEquals(
                expected = Resize(200, 200),
                actual = getResize()
            )
        }
    }

    @Test
    fun testGetTransformeds() {
        ImageCacheValue(image = FakeImage(100, 100)).apply {
            assertEquals(
                expected = null,
                actual = getTransformeds()
            )
        }

        ImageCacheValue(
            image = FakeImage(100, 100), extras = newCacheValueExtras(
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                resize = Resize(200, 200),
                transformeds = null,
                extras = null
            )
        ).apply {
            assertEquals(
                expected = null,
                actual = getTransformeds()
            )
        }

        ImageCacheValue(
            image = FakeImage(100, 100), extras = newCacheValueExtras(
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                resize = Resize(200, 200),
                transformeds = listOf(createRotateTransformed(90)),
                extras = null
            )
        ).apply {
            assertEquals(
                expected = listOf(createRotateTransformed(90)),
                actual = getTransformeds()
            )
        }
    }

    @Test
    fun testGetExtras() {
        ImageCacheValue(image = FakeImage(100, 100)).apply {
            assertEquals(
                expected = null,
                actual = getExtras()
            )
        }

        ImageCacheValue(
            image = FakeImage(100, 100), extras = newCacheValueExtras(
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                resize = Resize(200, 200),
                transformeds = null,
                extras = null
            )
        ).apply {
            assertEquals(
                expected = null,
                actual = getExtras()
            )
        }

        ImageCacheValue(
            image = FakeImage(100, 100), extras = newCacheValueExtras(
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                resize = Resize(200, 200),
                transformeds = null,
                extras = mapOf("key" to "value")
            )
        ).apply {
            assertEquals(
                expected = mapOf("key" to "value"),
                actual = getExtras()
            )
        }
    }

    @Test
    fun testNewCacheValueExtras() {
        assertEquals(
            expected = mapOf(
                "imageInfo" to ImageInfo(100, 100, "image/jpeg"),
                "resize" to Resize(200, 200),
                "transformeds" to listOf(createRotateTransformed(90)),
                "extras" to mapOf("key" to "value")
            ),
            actual = newCacheValueExtras(
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                resize = Resize(200, 200),
                transformeds = listOf(createRotateTransformed(90)),
                extras = mapOf("key" to "value")
            )
        )
    }
}