package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.appCacheDirectory
import platform.Foundation.NSBundle
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSPicturesDirectory
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual fun buildPlatformDirectoryItemList(context: PlatformContext): List<DirectoryItem> =
    buildList {
        val userPicturesDirectory =
            NSSearchPathForDirectoriesInDomains(NSPicturesDirectory, NSUserDomainMask, true)
                .firstOrNull()?.let { it as String }
        add(DirectoryItem("userPicturesDirectory", userPicturesDirectory))

        val userCachesDirectory =
            NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
                .firstOrNull()?.let { it as String }
        add(DirectoryItem("userCachesDirectory", userCachesDirectory))

        val mainBundle = NSBundle.mainBundle
        val bundleIdentifier = mainBundle.bundleIdentifier
        add(DirectoryItem("bundleIdentifier", bundleIdentifier))

        val bundleURL = mainBundle.bundleURL.toString()
        add(DirectoryItem("bundleURL", bundleURL))

        val bundlePath = mainBundle.bundlePath
        add(DirectoryItem("bundlePath", bundlePath))

        val processName = NSProcessInfo.processInfo.processName
        add(DirectoryItem("processName", processName))

        val appCacheDirectory = context.appCacheDirectory()?.toString()
        add(DirectoryItem("appCacheDirectory", appCacheDirectory))
    }