package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.updateImageOptions
import com.github.panpf.sketch.test.utils.TestImageOptionsProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ImageOptionsProviderTest {

    @Test
    fun testUpdateImageOptions() {
        val imageOptionsProvider = TestImageOptionsProvider()
        assertNull(imageOptionsProvider.imageOptions)

        imageOptionsProvider.updateImageOptions {
            memoryCachePolicy(CachePolicy.DISABLED)
        }
        assertEquals(
            expected = ImageOptions { memoryCachePolicy(CachePolicy.DISABLED) },
            actual = imageOptionsProvider.imageOptions
        )

        imageOptionsProvider.updateImageOptions {
            memoryCachePolicy(CachePolicy.DISABLED)
            downloadCachePolicy(CachePolicy.READ_ONLY)
        }
        assertEquals(
            expected = ImageOptions {
                memoryCachePolicy(CachePolicy.DISABLED)
                downloadCachePolicy(CachePolicy.READ_ONLY)
            },
            actual = imageOptionsProvider.imageOptions
        )
    }
}