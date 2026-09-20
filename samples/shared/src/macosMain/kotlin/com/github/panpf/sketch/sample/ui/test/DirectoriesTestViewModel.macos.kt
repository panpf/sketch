package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.AppInfos
import com.github.panpf.sketch.util.AppDirs
import com.github.panpf.sketch.util.appCacheDirectory
import platform.Foundation.NSBundle

actual fun buildPlatformDirectoryItemList(
    context: PlatformContext
): List<DirectoryItem> = buildList {
    // Debug: '/Users/panpf/Library/Caches/SketchImageLoader/1bf4ceab56351085f7f623ebf83041fc'
    // Release: '/Users/panpf/Library/Caches/com.github.panpf.sketch.sample'
    val defaultAppCacheDirectory = context.appCacheDirectory()?.toString()
    add(DirectoryItem("defaultAppCacheDirectory", defaultAppCacheDirectory))

    // Debug: ''
    // Release: 'com.github.panpf.sketch.sample'
    val mainBundle = NSBundle.mainBundle
    val bundleIdentifier = mainBundle.bundleIdentifier
    add(DirectoryItem("bundleIdentifier", bundleIdentifier))

    // Debug: '/Users/panpf/Workspace/sketch/samples/macosApp/build/bin/macosArm64/debugExecutable'
    // Release: '/Users/panpf/Downloads/Sketch Sample.app'
    val bundlePath = mainBundle.bundlePath
    add(DirectoryItem("bundlePath", bundlePath))

    val appCacheDirectory = AppDirs.getCacheDir(appName = AppInfos.SAMPLE_APP_NAME)
    add(DirectoryItem("appCacheDirectory", appCacheDirectory))

    val appDataDirectory = AppDirs.getDataDir(appName = AppInfos.SAMPLE_APP_NAME)
    add(DirectoryItem("appDataDirectory", appDataDirectory))

    val appConfigDirectory = AppDirs.getConfigDir(appName = AppInfos.SAMPLE_APP_NAME)
    add(DirectoryItem("appConfigDirectory", appConfigDirectory))

    val appDesktopDirectory = AppDirs.getDesktopDir(appName = AppInfos.SAMPLE_APP_NAME)
    add(DirectoryItem("appDesktopDirectory", appDesktopDirectory))

    val appDocumentsDirectory = AppDirs.getDocumentsDir(appName = AppInfos.SAMPLE_APP_NAME)
    add(DirectoryItem("appDocumentsDirectory", appDocumentsDirectory))

    val appDownloadsDirectory = AppDirs.getDownloadsDir(appName = AppInfos.SAMPLE_APP_NAME)
    add(DirectoryItem("appDownloadsDirectory", appDownloadsDirectory))

    val appPicturesDirectory = AppDirs.getPicturesDir(appName = AppInfos.SAMPLE_APP_NAME)
    add(DirectoryItem("appPicturesDirectory", appPicturesDirectory))

    val appMusicDirectory = AppDirs.getMusicDir(appName = AppInfos.SAMPLE_APP_NAME)
    add(DirectoryItem("appMusicDirectory", appMusicDirectory))

    val appVideosDirectory = AppDirs.getVideosDir(appName = AppInfos.SAMPLE_APP_NAME)
    add(DirectoryItem("appVideosDirectory", appVideosDirectory))
}