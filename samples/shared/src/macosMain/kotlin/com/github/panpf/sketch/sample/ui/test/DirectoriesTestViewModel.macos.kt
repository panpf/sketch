package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.appCacheDirectory
import platform.Foundation.NSBundle
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDesktopDirectory
import platform.Foundation.NSDownloadsDirectory
import platform.Foundation.NSMoviesDirectory
import platform.Foundation.NSMusicDirectory
import platform.Foundation.NSPicturesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual fun buildPlatformDirectoryItemList(
    context: PlatformContext
): List<DirectoryItem> = buildList {
    // Debug: '/Users/panpf/Library/Caches/SketchImageLoader/1bf4ceab56351085f7f623ebf83041fc'
    // Release: '/Users/panpf/Library/Caches/com.github.panpf.sketch.sample'
    val appCacheDirectory = context.appCacheDirectory()?.toString()
    add(DirectoryItem("appCacheDirectory", appCacheDirectory))

    // Debug: ''
    // Release: 'com.github.panpf.sketch.sample'
    val mainBundle = NSBundle.mainBundle
    val bundleIdentifier = mainBundle.bundleIdentifier
    add(DirectoryItem("bundleIdentifier", bundleIdentifier))

    // Debug: '/Users/panpf/Workspace/sketch/samples/macosApp/build/bin/macosArm64/debugExecutable'
    // Release: '/Users/panpf/Downloads/Sketch Sample.app'
    val bundlePath = mainBundle.bundlePath
    add(DirectoryItem("bundlePath", bundlePath))

    // Debug: '/Users/panpf/Library/Caches'
    // Release: '/Users/panpf/Library/Caches'
    val userCachesDirectory =
        NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
            .firstOrNull()?.let { it as String }
    add(DirectoryItem("userCachesDirectory", userCachesDirectory))

    // Debug: '/Users/panpf/Desktop'
    // Release: '/Users/panpf/Desktop'
    val userDesktopDirectory =
        NSSearchPathForDirectoriesInDomains(NSDesktopDirectory, NSUserDomainMask, true)
            .firstOrNull()?.let { it as String }
    add(DirectoryItem("userDesktopDirectory", userDesktopDirectory))

    // Debug: '/Users/panpf/Downloads'
    // Release: '/Users/panpf/Downloads'
    val userDownloadsDirectory =
        NSSearchPathForDirectoriesInDomains(NSDownloadsDirectory, NSUserDomainMask, true)
            .firstOrNull()?.let { it as String }
    add(DirectoryItem("userDownloadsDirectory", userDownloadsDirectory))

    // Debug: '/Users/panpf/Pictures'
    // Release: '/Users/panpf/Pictures'
    val userPicturesDirectory =
        NSSearchPathForDirectoriesInDomains(NSPicturesDirectory, NSUserDomainMask, true)
            .firstOrNull()?.let { it as String }
    add(DirectoryItem("userPicturesDirectory", userPicturesDirectory))

    // Debug: '/Users/panpf/Music'
    // Release: '/Users/panpf/Music'
    val userMusicDirectory =
        NSSearchPathForDirectoriesInDomains(NSMusicDirectory, NSUserDomainMask, true)
            .firstOrNull()?.let { it as String }
    add(DirectoryItem("userMusicDirectory", userMusicDirectory))

    // Debug: '/Users/panpf/Movies'
    // Release: '/Users/panpf/Movies'
    val userMoviesDirectory =
        NSSearchPathForDirectoriesInDomains(NSMoviesDirectory, NSUserDomainMask, true)
            .firstOrNull()?.let { it as String }
    add(DirectoryItem("userMoviesDirectory", userMoviesDirectory))
}