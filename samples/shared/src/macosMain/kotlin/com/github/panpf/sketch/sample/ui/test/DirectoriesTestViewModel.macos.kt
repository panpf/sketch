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
        // Debug: '/Users/panpf/Pictures'
        // Release: '/Users/panpf/Pictures'
        val userPicturesDirectory =
            NSSearchPathForDirectoriesInDomains(NSPicturesDirectory, NSUserDomainMask, true)
                .firstOrNull()?.let { it as String }
        add(DirectoryItem("userPicturesDirectory", userPicturesDirectory))

        // Debug: '/Users/panpf/Library/Caches'
        // Release: '/Users/panpf/Library/Caches'
        val userCachesDirectory =
            NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
                .firstOrNull()?.let { it as String }
        add(DirectoryItem("userCachesDirectory", userCachesDirectory))

        // Debug: ''
        // Release: 'com.github.panpf.sketch.sample'
        val mainBundle = NSBundle.mainBundle
        val bundleIdentifier = mainBundle.bundleIdentifier
        add(DirectoryItem("bundleIdentifier", bundleIdentifier))

        // Debug: 'file:///Users/panpf/Workspace/sketch/samples/macosApp/build/bin/macosArm64/debugExecutable/'
        // Release: 'file:///Users/panpf/Downloads/Sketch4.app/'
        val bundleURL = mainBundle.bundleURL.toString()
        add(DirectoryItem("bundleURL", bundleURL))

        // Debug: '/Users/panpf/Workspace/sketch/samples/macosApp/build/bin/macosArm64/debugExecutable'
        // Release: '/Users/panpf/Downloads/Sketch4.app'
        val bundlePath = mainBundle.bundlePath
        add(DirectoryItem("bundlePath", bundlePath))

        // Debug: 'macosApp.kexe'
        // Release: 'Sketch4'
        val processName = NSProcessInfo.processInfo.processName
        add(DirectoryItem("processName", processName))

        // Debug: '/Users/panpf/Library/Caches/macosApp.kexe'
        // Release: '/Users/panpf/Library/Caches/com.github.panpf.sketch.sample'
        val appCacheDirectory = context.appCacheDirectory()?.toString()
        add(DirectoryItem("appCacheDirectory", appCacheDirectory))
    }