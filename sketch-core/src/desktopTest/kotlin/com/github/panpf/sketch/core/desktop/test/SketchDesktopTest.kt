package com.github.panpf.sketch.core.desktop.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.SkiaDecoder
import com.github.panpf.sketch.fetch.KotlinResourceUriFetcher
import com.github.panpf.sketch.platformComponents
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class SketchDesktopTest {

    @Test
    fun testPlatformComponents() {
        val context = getTestContext()
        assertEquals(
            expected = ComponentRegistry {
                addFetcher(KotlinResourceUriFetcher.Factory())
                addDecoder(SkiaDecoder.Factory())
            },
            actual = platformComponents(context)
        )
    }
}