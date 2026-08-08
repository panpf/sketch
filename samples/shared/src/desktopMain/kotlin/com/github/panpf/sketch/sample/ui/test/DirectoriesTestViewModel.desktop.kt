package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.appCacheDirectory
import net.harawata.appdirs.AppDirsFactory
import java.net.URI
import java.nio.file.Paths

actual fun buildPlatformDirectoryItemList(context: PlatformContext): List<DirectoryItem> =
    buildList {
        // macOS: Debug: hotReload: '/Users/panpf/Library/Caches'
        // macOS: Debug: normal: '/Users/panpf/Library/Caches'
        // macOS: Release: normal: '/Users/panpf/Library/Caches'
        val userCacheDirectory = AppDirsFactory.getInstance()
            .getUserCacheDir(/* appName = */ null,/* appVersion = */ null,/* appAuthor = */ null)
        add(DirectoryItem("userCacheDirectory", userCacheDirectory))

        // macOS: Debug: hotReload: '/Users/panpf/Library/Application Support'
        // macOS: Debug: normal: '/Users/panpf/Library/Application Support'
        // macOS: Release: normal: '/Users/panpf/Library/Application Support'
        val userDataDirectory = AppDirsFactory.getInstance()
            .getUserDataDir(/* appName = */ null,/* appVersion = */ null,/* appAuthor = */ null)
        add(DirectoryItem("userDataDirectory", userDataDirectory))

        // macOS: Debug: hotReload: '/Users/panpf/Library/Preferences'
        // macOS: Debug: normal: '/Users/panpf/Library/Preferences'
        // macOS: Release: normal: '/Users/panpf/Library/Preferences'
        val userConfigDirectory = AppDirsFactory.getInstance()
            .getUserConfigDir(/* appName = */ null,/* appVersion = */ null,/* appAuthor = */ null)
        add(DirectoryItem("userConfigDirectory", userConfigDirectory))

        // macOS: Debug: hotReload: ''
        // macOS: Debug: normal: '/Users/panpf/Workspace/sketch/samples/desktopApp/build/compose/tmp/prepareAppResources'
        // macOS: Release: normal: '/Users/panpf/Downloads/Sketch4.app/Contents/app/resources'
        val composeResourcesPath = getComposeResourcesPath()
        add(DirectoryItem("composeResourcesPath", composeResourcesPath))

        // macOS: Debug. hotReload: '/Users/panpf/Workspace/sketch/samples/desktopApp/build/run/desktopMain/classpath/libs/sketch-core/9d92mnc/sketch-core-desktop-4.7.0-alpha01.jar'
        // macOS: Debug. normal: '/Users/panpf/Workspace/sketch/sketch-core/build/libs/sketch-core-desktop-4.7.0-alpha01.jar'
        // macOS: Release. normal: '/Users/panpf/Downloads/Sketch4.app/Contents/app/sketch-core-desktop-4.7.0-alpha01-6687c2e433cc6cca19d38f36afe9a9d4.jar'
        val jarPath = getJarPath(PlatformContext::class.java)
        add(DirectoryItem("jarPath", jarPath))

        // macOS: Debug: hotReload: '/Users/panpf/Library/Caches/SketchImageLoader/ece624f7de942a10408f90b4ef73d207'
        // macOS: Debug: normal: '/Users/panpf/Library/Caches/SketchImageLoader/a153ef8d1e6750f396f0582b25329ddb'
        // macOS: Release: normal: '/Users/panpf/Library/Caches/SketchImageLoader/5fc3f0cb597bdebc54a2877d2c033920'
        val appCacheDirectory = context.appCacheDirectory()?.toString()
        add(DirectoryItem("appCacheDirectory", appCacheDirectory))
    }

/**
 * Returns the path to the app's compose resources directory. Only works in release mode
 *
 * Example:
 * macOs: '/Applications/hellokmp.app/Contents/app/resources'
 * Windows: 'C:\Program Files\hellokmp\app\resources'
 * Linux: '/opt/hellokmp/lib/app/resources'
 * dev: null
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.JvmUtilsTest.testGetComposeResourcesPath
 */
internal fun getComposeResourcesPath(): String? {
    return System.getProperty("compose.application.resources.dir")
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
}

/**
 * Returns the path to the jar file where the specified class is located
 *
 * Example:
 * macOs: '/Applications/hellokmp.app/Contents/app/composeApp-desktop-f6c789dab561fea8ab3a9533b659d11a.jar'
 * Windows: 'C:\Program Files\hellokmp\app\composeApp-desktop-55c1f2d3ee1433be9f95b1912fbd.jar'
 * Linux: '/opt/hellokmp/lib/app/composeApp-desktop-e1e452276759301f909baa97e6a11ff4.jar'
 * dev: '/Users/panpf/Workspace/KotlinProjectDesktop/composeApp/build/classes/kotlin/desktop/main'
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.JvmUtilsTest.testGetJarPath
 */
internal fun getJarPath(aclass: Class<*>): String? {
    try {
        val codeSource = aclass.protectionDomain.codeSource
        if (codeSource != null) {
            val location: URI = codeSource.location.toURI()
            return Paths.get(location).toString()
        } else {
            return null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}