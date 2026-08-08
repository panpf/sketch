package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.appCacheDirectory

actual fun buildPlatformDirectoryItemList(context: PlatformContext): List<DirectoryItem> =
    buildList {
        val appExternalCacheDir = context.externalCacheDir?.toString()
        add(DirectoryItem("appExternalCacheDir", appExternalCacheDir))

        val appExternalFilesDir = context.getExternalFilesDir(null)?.toString()
        add(DirectoryItem("appExternalFilesDir", appExternalFilesDir))

        val appCacheDirectory = context.appCacheDirectory()?.toString()
        add(DirectoryItem("appCacheDirectory", appCacheDirectory))
    }