package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.AppDirs
import com.github.panpf.sketch.util.appCacheDirectory
import java.net.URI
import java.nio.file.Paths

actual fun buildPlatformDirectoryItemList(context: PlatformContext): List<DirectoryItem> =
    buildList {
        // windows: Debug (normal): 'C:\Users\panpf\AppData\Local\SketchImageLoader\a519cef9835e9be1cddb14770a1b6ff9\Cache'
        // windows: Debug (hotReload): 'C:\Users\panpf\AppData\Local\SketchImageLoader\cb7785e11486b2233bb9f35f7bec2c77\Cache'
        // windows: Release: 'C:\Users\panpf\AppData\Local\SketchImageLoader\e26c0c487c4bb4e45663c35c4400cae1\Cache'
        // macOS: Debug (normal): '/Users/panpf/Library/Caches/SketchImageLoader/f99742e39ecdb5444589ad6aa2eff27a'
        // macOS: Debug (hotReload): '/Users/panpf/Library/Caches/SketchImageLoader/3120c807db80a0e963f8c0073ec54f37'
        // macOS: Release: '/Users/panpf/Library/Caches/SketchImageLoader/3a852cc448c41fefdf3b19c26497f70b'
        // linux: Debug (normal): '/Users/panpf/.cache/SketchImageLoader/f99742e39ecdb5444589ad6aa2eff27a'
        // linux: Debug (hotReload): '/Users/.cache/SketchImageLoader/3120c807db80a0e963f8c0073ec54f37'
        // linux: Release: '/Users/panpf/.cache/SketchImageLoader/3a852cc448c41fefdf3b19c26497f70b'
        val appCacheDirectory = context.appCacheDirectory()?.toString()
        add(DirectoryItem("appCacheDirectory", appCacheDirectory))

        // windows: Debug (normal): 'D:\Developer\Workspace\sketch\sketch-core\build\libs\sketch-core-desktop-4.7.0-alpha01.jar'
        // windows: Debug (hotReload): 'D:\Developer\Workspace\sketch\samples\desktopApp\build\run\desktopMain\classpath\libs\sketch-core\9d92mnc\sketch-core-desktop-4.7.0-alpha01.jar'
        // windows: Release: 'C:\Program Files\Sketch4\app\sketch-core-desktop-4.7.0-alpha01-6687c2e433cc6cca19d38f36afe9a9d4.jar'
        // macOS: Debug (normal): '/Users/panpf/Workspace/sketch/sketch-core/build/libs/sketch-core-desktop-4.7.0-alpha01.jar'
        // macOS: Debug (hotReload): '/Users/panpf/Workspace/sketch/samples/desktopApp/build/run/desktopMain/classpath/libs/sketch-core/9d92mnc/sketch-core-desktop-4.7.0-alpha01.jar'
        // macOS: Release: '/Users/panpf/Downloads/Sketch4.app/Contents/app/sketch-core-desktop-4.7.0-alpha01-6687c2e433cc6cca19d38f36afe9a9d4.jar'
        // linux: Debug (normal): '/home/panpf/Workspace/sketch/sketch-core/build/libs/sketch-core-desktop-4.7.0-alpha01.jar'
        // linux: Debug (hotReload): '/home/panpf/Workspace/sketch/samples/desktopApp/build/run/desktopMain/classpath/libs/sketch-core/9d92mnc/sketch-core-desktop-4.7.0-alpha01.jar'
        // linux: '/opt/hellokmp/lib/app/composeApp-desktop-e1e452276759301f909baa97e6a11ff4.jar'
        val jarPath = getJarPath(PlatformContext::class.java)
        add(DirectoryItem("jarPath", jarPath))

        // windows: Debug (normal): ''
        // windows: Debug (hotReload): ''
        // windows: Release: 'C:\Program Files\Sketch4\app\resources'
        // macOS: Debug (normal): '/Users/panpf/Workspace/sketch/samples/desktopApp/build/compose/tmp/prepareAppResources'
        // macOS: Debug (hotReload): ''
        // macOS: Release: '/Users/panpf/Downloads/Sketch4.app/Contents/app/resources'
        val composeResourcesPath = getComposeResourcesPath()
        add(DirectoryItem("composeResourcesPath", composeResourcesPath))

        val userCacheDirectory = AppDirs.getCacheDir(appName = null)
        add(DirectoryItem("userCacheDirectory", userCacheDirectory.toString()))

        val userDataDirectory = AppDirs.getDataDir(appName = null)
        add(DirectoryItem("userDataDirectory", userDataDirectory.toString()))

        val userConfigDirectory = AppDirs.getConfigDir(appName = null)
        add(DirectoryItem("userConfigDirectory", userConfigDirectory.toString()))

        val userDesktopDirectory = AppDirs.getDesktopDir(appName = null)
        add(DirectoryItem("userDesktopDirectory", userDesktopDirectory.toString()))

        val userDocumentsDirectory = AppDirs.getDocumentsDir(appName = null)
        add(DirectoryItem("userDocumentsDirectory", userDocumentsDirectory.toString()))

        val userDownloadsDirectory = AppDirs.getDownloadsDir(appName = null)
        add(DirectoryItem("userDownloadsDirectory", userDownloadsDirectory.toString()))

        val userPicturesDirectory = AppDirs.getPicturesDir(appName = null)
        add(DirectoryItem("userPicturesDirectory", userPicturesDirectory.toString()))

        val userMusicDirectory = AppDirs.getMusicDir(appName = null)
        add(DirectoryItem("userMusicDirectory", userMusicDirectory.toString()))

        val userVideosDirectory = AppDirs.getVideosDir(appName = null)
        add(DirectoryItem("userVideosDirectory", userVideosDirectory.toString()))
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