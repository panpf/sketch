package com.github.panpf.sketch.core.ios.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.PhotosAssetDecoder
import com.github.panpf.sketch.decode.SkiaDecoder
import com.github.panpf.sketch.decode.internal.UseSkiaInterceptor
import com.github.panpf.sketch.fetch.KotlinResourceUriFetcher
import com.github.panpf.sketch.fetch.PhotosAssetUriFetcher
import com.github.panpf.sketch.platformComponents
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class SketchIosTest {

    @Test
    fun testPlatformComponents() {
        val context = getTestContext()
        assertEquals(
            expected = ComponentRegistry {
                add(PhotosAssetUriFetcher.Factory())
                add(KotlinResourceUriFetcher.Factory())

                add(PhotosAssetDecoder.Factory())
                add(SkiaDecoder.Factory())

                add(UseSkiaInterceptor())
            },
            actual = platformComponents(context)
        )
    }
}