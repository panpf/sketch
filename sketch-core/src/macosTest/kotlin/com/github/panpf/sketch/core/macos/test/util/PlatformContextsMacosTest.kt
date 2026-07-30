package com.github.panpf.sketch.core.macos.test.util

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.appCacheDirectory
import com.github.panpf.sketch.util.maxMemory
import com.github.panpf.sketch.util.screenSize
import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PlatformContextsMacosTest {

    @Test
    fun testMaxMemory() {
        assertTrue(PlatformContext.INSTANCE.maxMemory() > 0L)
    }

    @Test
    fun testAppCacheDirectory() {
        val cacheDirectory = PlatformContext.INSTANCE.appCacheDirectory()
        assertNotNull(cacheDirectory)
        val applicationId = NSBundle.mainBundle.bundleIdentifier
            ?.takeIf { it.isNotBlank() }
            ?: NSProcessInfo.processInfo.processName
        assertTrue(cacheDirectory.toString().endsWith("Library/Caches/$applicationId"))
    }

    @Test
    fun testScreenSize() {
        val screenSize = PlatformContext.INSTANCE.screenSize()
        assertTrue(screenSize.width > 0)
        assertTrue(screenSize.height > 0)
        assertTrue(screenSize != Size(0, 0))
    }
}
