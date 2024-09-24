package com.github.panpf.sketch.core.desktop.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.SkiaDecoder
import com.github.panpf.sketch.fetch.KotlinResourceUriFetcher
import com.github.panpf.sketch.platformComponents
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
            assertTrue(networkTaskDispatcher.toString().startsWith("LimitedDispatcher"))
            @Suppress("UNCHECKED_CAST")
            val parallelismProperty: KProperty1<Any, Any> = networkTaskDispatcher::class
                .memberProperties.find { it.name == "parallelism" }!! as KProperty1<Any, Any>
            parallelismProperty.isAccessible = true
            assertEquals(10, parallelismProperty.get(networkTaskDispatcher) as Int)
        }

        Sketch.Builder(context).apply {
            networkParallelismLimited(20)
        }.build().apply {
            assertTrue(networkTaskDispatcher.toString().startsWith("LimitedDispatcher"))
            @Suppress("UNCHECKED_CAST")
            val parallelismProperty: KProperty1<Any, Any> = networkTaskDispatcher::class
                .memberProperties.find { it.name == "parallelism" }!! as KProperty1<Any, Any>
            parallelismProperty.isAccessible = true
            assertEquals(20, parallelismProperty.get(networkTaskDispatcher) as Int)
        }

        // decodeParallelismLimited
        Sketch.Builder(context).build().apply {
            assertTrue(decodeTaskDispatcher.toString().startsWith("LimitedDispatcher"))
            @Suppress("UNCHECKED_CAST")
            val parallelismProperty: KProperty1<Any, Any> = decodeTaskDispatcher::class
                .memberProperties.find { it.name == "parallelism" }!! as KProperty1<Any, Any>
            parallelismProperty.isAccessible = true
            assertEquals(4, parallelismProperty.get(decodeTaskDispatcher) as Int)
        }

        Sketch.Builder(context).apply {
            decodeParallelismLimited(8)
        }.build().apply {
            assertTrue(decodeTaskDispatcher.toString().startsWith("LimitedDispatcher"))
            @Suppress("UNCHECKED_CAST")
            val parallelismProperty: KProperty1<Any, Any> = decodeTaskDispatcher::class
                .memberProperties.find { it.name == "parallelism" }!! as KProperty1<Any, Any>
            parallelismProperty.isAccessible = true
            assertEquals(8, parallelismProperty.get(decodeTaskDispatcher) as Int)
        }
    }
}