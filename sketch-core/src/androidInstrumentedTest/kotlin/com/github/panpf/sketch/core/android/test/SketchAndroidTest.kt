package com.github.panpf.sketch.core.android.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch.Builder
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.platformComponents
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SketchAndroidTest {

    @Test
    fun testBuilder() {
        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            Builder(activity).build().apply {
                assertNotEquals(activity, context)
                assertEquals(activity.applicationContext, context)
            }
        }
    }

    @Test
    fun testPlatformComponents() {
        val context = getTestContext()
        assertEquals(
            expected = ComponentRegistry {
                addFetcher(ContentUriFetcher.Factory())
                addFetcher(ResourceUriFetcher.Factory())
                addFetcher(AssetUriFetcher.Factory())

                addDecoder(DrawableDecoder.Factory())
                addDecoder(BitmapFactoryDecoder.Factory())
            },
            actual = platformComponents(context)
        )
    }
}