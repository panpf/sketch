package com.github.panpf.sketch.core.ios.test.util

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.appCacheDirectory
import com.github.panpf.sketch.util.maxMemory
import com.github.panpf.sketch.util.screenSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIScreen
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformContextsIosTest {

    @Test
    fun testMaxMemory() {
        assertEquals(
            expected = 512L * 1024L * 1024L,
            actual = PlatformContext.INSTANCE.maxMemory(),
        )
    }

    @Test
    fun testAppCacheDirectory() {
        val paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
        val cacheDir = (paths.first() as String).toPath()
        assertEquals(
            expected = cacheDir,
            actual = PlatformContext.INSTANCE.appCacheDirectory(),
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun testScreenSize() {
        val screen = UIScreen.mainScreen
        val scale = screen.scale
        val width = screen.bounds.useContents { size.width }
        val height = screen.bounds.useContents { size.height }
        val widthPixels = (width * scale).roundToInt()
        val heightPixels = (height * scale).roundToInt()
        val screenSize = Size(width = widthPixels, height = heightPixels)
        assertEquals(
            expected = screenSize,
            actual = PlatformContext.INSTANCE.screenSize(),
        )
    }
}