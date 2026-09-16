package com.github.panpf.sketch.core.desktop.test.util

import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.isMacOS
import com.github.panpf.sketch.test.utils.isWindows
import com.github.panpf.sketch.util.AppDirs
import org.junit.Test
import kotlin.test.assertEquals

class AppDirsTest {

    private val appName = "SketchSample"
    private val useName = System.getProperty("user.name")!!

    @Test
    fun testGetConfigDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Roaming\\$appName"
                Platform.current.isMacOS -> "/Users/$useName/Library/Preferences/$appName"
                else -> "/home/$useName/.config/$appName"
            },
            actual = AppDirs.getConfigDir(appName = appName).path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Roaming"
                Platform.current.isMacOS -> "/Users/$useName/Library/Preferences"
                else -> "/home/$useName/.config"
            },
            actual = AppDirs.getConfigDir(appName = null).path,
        )
    }

    @Test
    fun testGetDataDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Local\\$appName"
                Platform.current.isMacOS -> "/Users/$useName/Library/Application Support/$appName"
                else -> "/home/$useName/.local/share/$appName"
            },
            actual = AppDirs.getDataDir(appName = appName).path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Local"
                Platform.current.isMacOS -> "/Users/$useName/Library/Application Support"
                else -> "/home/$useName/.local/share"
            },
            actual = AppDirs.getDataDir(appName = null).path,
        )
    }

    @Test
    fun testGetCacheDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Local\\$appName\\Cache"
                Platform.current.isMacOS -> "/Users/$useName/Library/Caches/$appName"
                else -> "/home/$useName/.cache/$appName"
            },
            actual = AppDirs.getCacheDir(appName = appName).path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Local\\Cache"
                Platform.current.isMacOS -> "/Users/$useName/Library/Caches"
                else -> "/home/$useName/.cache"
            },
            actual = AppDirs.getCacheDir(appName = null).path,
        )
    }

    @Test
    fun testGetStateDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Local\\$appName\\State"
                Platform.current.isMacOS -> "/Users/$useName/Library/Application Support/$appName/State"
                else -> "/home/$useName/.local/state/$appName"
            },
            actual = AppDirs.getStateDir(appName = appName).path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Local\\State"
                Platform.current.isMacOS -> "/Users/$useName/Library/Application Support/State"
                else -> "/home/$useName/.local/state"
            },
            actual = AppDirs.getStateDir(appName = null).path,
        )
    }

    @Test
    fun testGetRuntimeDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Local\\$appName\\Runtime"
                Platform.current.isMacOS -> "/Users/$useName/Library/Application Support/$appName/Runtime"
                else -> "/run/$useName/1000/$appName"
            },
            actual = AppDirs.getRuntimeDir(appName = appName)?.path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Local\\Runtime"
                Platform.current.isMacOS -> "/Users/$useName/Library/Application Support/Runtime"
                else -> "/run/$useName/1000"
            },
            actual = AppDirs.getRuntimeDir(appName = null)?.path,
        )
    }

    @Test
    fun testGetDesktopDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Desktop\\$appName"
                Platform.current.isMacOS -> "/Users/$useName/Desktop/$appName"
                else -> "/home/$useName/Desktop/$appName"
            },
            actual = AppDirs.getDesktopDir(appName = appName).path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Desktop"
                Platform.current.isMacOS -> "/Users/$useName/Desktop"
                else -> "/home/$useName/Desktop"
            },
            actual = AppDirs.getDesktopDir(appName = null).path,
        )
    }

    @Test
    fun testGetDocumentsDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Documents\\$appName"
                Platform.current.isMacOS -> "/Users/$useName/Documents/$appName"
                else -> "/home/$useName/Documents/$appName"
            },
            actual = AppDirs.getDocumentsDir(appName = appName).path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Documents"
                Platform.current.isMacOS -> "/Users/$useName/Documents"
                else -> "/home/$useName/Documents"
            },
            actual = AppDirs.getDocumentsDir(appName = null).path,
        )
    }

    @Test
    fun testGetDownloadsDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Downloads\\$appName"
                Platform.current.isMacOS -> "/Users/$useName/Downloads/$appName"
                else -> "/home/$useName/Downloads/$appName"
            },
            actual = AppDirs.getDownloadsDir(appName = appName).path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Downloads"
                Platform.current.isMacOS -> "/Users/$useName/Downloads"
                else -> "/home/$useName/Downloads"
            },
            actual = AppDirs.getDownloadsDir(appName = null).path,
        )
    }

    @Test
    fun testGetPicturesDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Pictures\\$appName"
                Platform.current.isMacOS -> "/Users/$useName/Pictures/$appName"
                else -> "/home/$useName/Pictures/$appName"
            },
            actual = AppDirs.getPicturesDir(appName = appName).path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Pictures"
                Platform.current.isMacOS -> "/Users/$useName/Pictures"
                else -> "/home/$useName/Pictures"
            },
            actual = AppDirs.getPicturesDir(appName = null).path,
        )
    }

    @Test
    fun testGetMusicDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Music\\$appName"
                Platform.current.isMacOS -> "/Users/$useName/Music/$appName"
                else -> "/home/$useName/Music/$appName"
            },
            actual = AppDirs.getMusicDir(appName = appName).path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Music"
                Platform.current.isMacOS -> "/Users/$useName/Music"
                else -> "/home/$useName/Music"
            },
            actual = AppDirs.getMusicDir(appName = null).path,
        )
    }

    @Test
    fun testGetVideosDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Videos\\$appName"
                Platform.current.isMacOS -> "/Users/$useName/Videos/$appName"
                else -> "/home/$useName/Videos/$appName"
            },
            actual = AppDirs.getVideosDir(appName = appName).path,
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Videos"
                Platform.current.isMacOS -> "/Users/$useName/Videos"
                else -> "/home/$useName/Videos"
            },
            actual = AppDirs.getVideosDir(appName = null).path,
        )
    }
}