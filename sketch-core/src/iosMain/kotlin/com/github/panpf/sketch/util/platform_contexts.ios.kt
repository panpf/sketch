package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIScreen
import kotlin.math.roundToInt

actual fun PlatformContext.appCacheDirectory(): Path? {
    return getCacheDirectory().toPath()
}

private fun getCacheDirectory(): String {
    val paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
    return paths.first() as String
}

@OptIn(ExperimentalForeignApi::class)
actual fun PlatformContext.screenSize(): Size {
    val screen = UIScreen.mainScreen
    val scale = screen.scale
    val width = screen.bounds.useContents { size.width }
    val height = screen.bounds.useContents { size.height }
    val widthPixels = (width * scale).roundToInt()
    val heightPixels = (height * scale).roundToInt()
    return Size(width = widthPixels, height = heightPixels)
}