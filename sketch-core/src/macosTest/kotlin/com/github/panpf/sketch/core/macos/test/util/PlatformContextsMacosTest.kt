package com.github.panpf.sketch.core.macos.test.util

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.AppDirs
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.appCacheDirectory
import com.github.panpf.sketch.util.maxMemory
import com.github.panpf.sketch.util.md5
import com.github.panpf.sketch.util.screenSize
import platform.Foundation.NSBundle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlatformContextsMacosTest {

    @Test
    fun testMaxMemory() {
        assertTrue(PlatformContext.INSTANCE.maxMemory() > 0L)
    }

    @Test
    fun testAppCacheDirectory() {
        val appId = NSBundle.mainBundle.bundleIdentifier?.takeIf { it.isNotEmpty() }
            ?: NSBundle.mainBundle.bundlePath.takeIf { it.isNotEmpty() }
                ?.substringBefore("/build/")
                ?.md5()
                ?.let { "SketchImageLoader/${it}" }
        if (appId != null) {
            assertTrue(
                actual = !appId.contains("/build/"),
                message = "appId: $appId"
            )
        }
        val currentCacheDirectory = if (appId != null) AppDirs.getCacheDir(appId) else null

        val cacheDirectory = PlatformContext.INSTANCE.appCacheDirectory()?.toString()
        assertEquals(expected = currentCacheDirectory, actual = cacheDirectory)
        assertTrue(
            actual = cacheDirectory.orEmpty().endsWith("/Library/Caches/${appId}"),
            message = cacheDirectory
        )
    }

    @Test
    fun testScreenSize() {
        val screenSize = PlatformContext.INSTANCE.screenSize()
        assertTrue(screenSize.width > 0)
        assertTrue(screenSize.height > 0)
        assertTrue(screenSize != Size(0, 0))
    }
}
