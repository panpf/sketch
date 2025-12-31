package com.github.panpf.sketch.core.desktop.test.util

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.isWindows
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.appCacheDirectory
import com.github.panpf.sketch.util.getComposeResourcesPath
import com.github.panpf.sketch.util.getJarPath
import com.github.panpf.sketch.util.maxMemory
import com.github.panpf.sketch.util.md5
import com.github.panpf.sketch.util.screenSize
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformContextsDesktopTest {

    @Test
    fun testMaxMemory() {
        assertEquals(
            expected = Runtime.getRuntime().maxMemory(),
            actual = PlatformContext.INSTANCE.maxMemory(),
        )
    }

    @Test
    fun testAppCacheDirectory() {
        val appFlag = (getComposeResourcesPath() ?: getJarPath(Sketch::class.java))
            ?.md5()
        val fakeAppName = "SketchImageLoader${File.separator}${appFlag}"
        val appCacheDir = PlatformContext.INSTANCE.appCacheDirectory().toString()
        if (!Platform.current.isWindows) {
            assertEquals(
                message = "appCacheDir: $appCacheDir, fakeAppName: $fakeAppName",
                expected = true,
                actual = appCacheDir.endsWith(fakeAppName),
            )
        } else {
            assertEquals(
                message = "appCacheDir: $appCacheDir, fakeAppName: $fakeAppName",
                expected = true,
                actual = appCacheDir.endsWith("$fakeAppName\\Cache"),
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
    }
}