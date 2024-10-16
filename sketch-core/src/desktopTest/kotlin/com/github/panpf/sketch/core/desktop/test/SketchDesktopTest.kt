package com.github.panpf.sketch.core.desktop.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
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

    @Test
    fun testBuilder() {
        val context = getTestContext()

        // networkParallelismLimited
        Sketch.Builder(context).build().apply {
            assertEquals(
                expected = "Dispatchers.IO.limitedParallelism(10)",
                actual = networkTaskDispatcher.toString()
            )
        }

        Sketch.Builder(context).apply {
            networkParallelismLimited(20)
        }.build().apply {
            assertEquals(
                expected = "Dispatchers.IO.limitedParallelism(20)",
                actual = networkTaskDispatcher.toString()
            )
        }

        // decodeParallelismLimited
        Sketch.Builder(context).build().apply {
            assertEquals(
                expected = "Dispatchers.IO.limitedParallelism(4)",
                actual = decodeTaskDispatcher.toString()
            )
        }

        Sketch.Builder(context).apply {
            decodeParallelismLimited(8)
        }.build().apply {
            assertEquals(
                expected = "Dispatchers.IO.limitedParallelism(8)",
                actual = decodeTaskDispatcher.toString()
            )
        }
    }
}