package com.github.panpf.sketch.core.jvm.test.util

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.isLinux
import com.github.panpf.sketch.test.utils.isMacOS
import com.github.panpf.sketch.test.utils.isWindows
import com.github.panpf.sketch.util.AppDirs
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.appCacheDirectory
import com.github.panpf.sketch.util.getJarPath
import com.github.panpf.sketch.util.maxMemory
import com.github.panpf.sketch.util.md5
import com.github.panpf.sketch.util.screenSize
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PlatformContextsJvmTest {

    @Test
    fun testMaxMemory() {
        assertEquals(
            expected = Runtime.getRuntime().maxMemory(),
            actual = PlatformContext.INSTANCE.maxMemory(),
        )
    }

    @Test
    fun testAppCacheDirectory() {
        val appId = getJarPath(PlatformContext::class.java)
            ?.substringBefore("${File.separator}build${File.separator}")    // Debug
            ?.substringBefore("${File.separator}app${File.separator}")  // Release
            ?.md5()
            ?.let { "SketchImageLoader${File.separator}${it}" }
        if (appId != null) {
            assertTrue(
                actual = !appId.contains("${File.separator}build${File.separator}")
                        && !appId.contains("${File.separator}app${File.separator}"),
                message = "appId: $appId"
            )
        }

        val currentAppCacheDir = if (appId != null)
            AppDirs.getCacheDir(appId).toString() else null

        val appCacheDir = PlatformContext.INSTANCE.appCacheDirectory()?.toString()
        assertEquals(currentAppCacheDir, appCacheDir)
        if (Platform.current.isMacOS) {
            assertTrue(
                actual = appCacheDir.orEmpty().endsWith("Library/Caches/$appId"),
                message = "appCacheDir: $appCacheDir"
            )
        } else if (Platform.current.isWindows) {
            assertTrue(
                actual = appCacheDir.orEmpty().endsWith("AppData\\Local\\$appId\\Cache"),
                message = "appCacheDir: $appCacheDir"
            )
        } else if (Platform.current.isLinux) {
            assertTrue(
                actual = appCacheDir.orEmpty().endsWith(".cache/$appId"),
                message = "appCacheDir: $appCacheDir"
            )
        }
    }

    @Test
    fun testScreenSize() {
        val screenSize = java.awt.Toolkit.getDefaultToolkit().screenSize
            .let { Size(it.width, it.height) }
        assertEquals(
            expected = screenSize,
            actual = PlatformContext.INSTANCE.screenSize(),
        )
        assertNotEquals(
            illegal = Size(0, 0),
            actual = PlatformContext.INSTANCE.screenSize(),
        )
    }
}