package com.github.panpf.sketch.core.jvm.test.util

import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.isMacOS
import com.github.panpf.sketch.test.utils.isWindows
import com.github.panpf.sketch.util.AppDirs
import kotlin.test.Test
import kotlin.test.assertEquals

class AppDirsJvmTest {

    private val appName = "Sketch Sample"
    private val useName = System.getProperty("user.name")!!

    @Test
    fun testGetConfigDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Roaming\\$appName"
                Platform.current.isMacOS -> "/Users/$useName/Library/Preferences/$appName"
                else -> "/home/$useName/.config/$appName"
            },
            actual = AppDirs.getConfigDir(appName = appName),
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Roaming"
                Platform.current.isMacOS -> "/Users/$useName/Library/Preferences"
                else -> "/home/$useName/.config"
            },
            actual = AppDirs.getConfigDir(appName = null),
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
            actual = AppDirs.getDataDir(appName = appName),
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Local"
                Platform.current.isMacOS -> "/Users/$useName/Library/Application Support"
                else -> "/home/$useName/.local/share"
            },
            actual = AppDirs.getDataDir(appName = null),
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
            actual = AppDirs.getCacheDir(appName = appName),
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\AppData\\Local\\Cache"
                Platform.current.isMacOS -> "/Users/$useName/Library/Caches"
                else -> "/home/$useName/.cache"
            },
            actual = AppDirs.getCacheDir(appName = null),
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
            actual = AppDirs.getDesktopDir(appName = appName),
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Desktop"
                Platform.current.isMacOS -> "/Users/$useName/Desktop"
                else -> "/home/$useName/Desktop"
            },
            actual = AppDirs.getDesktopDir(appName = null),
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
            actual = AppDirs.getDocumentsDir(appName = appName),
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Documents"
                Platform.current.isMacOS -> "/Users/$useName/Documents"
                else -> "/home/$useName/Documents"
            },
            actual = AppDirs.getDocumentsDir(appName = null),
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
            actual = AppDirs.getDownloadsDir(appName = appName),
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Downloads"
                Platform.current.isMacOS -> "/Users/$useName/Downloads"
                else -> "/home/$useName/Downloads"
            },
            actual = AppDirs.getDownloadsDir(appName = null),
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
            actual = AppDirs.getPicturesDir(appName = appName),
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Pictures"
                Platform.current.isMacOS -> "/Users/$useName/Pictures"
                else -> "/home/$useName/Pictures"
            },
            actual = AppDirs.getPicturesDir(appName = null),
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
            actual = AppDirs.getMusicDir(appName = appName),
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Music"
                Platform.current.isMacOS -> "/Users/$useName/Music"
                else -> "/home/$useName/Music"
            },
            actual = AppDirs.getMusicDir(appName = null),
        )
    }

    @Test
    fun testGetVideosDir() {
        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Videos\\$appName"
                Platform.current.isMacOS -> "/Users/$useName/Movies/$appName"
                else -> "/home/$useName/Videos/$appName"
            },
            actual = AppDirs.getVideosDir(appName = appName),
        )

        assertEquals(
            expected = when {
                Platform.current.isWindows -> "C:\\Users\\$useName\\Videos"
                Platform.current.isMacOS -> "/Users/$useName/Movies"
                else -> "/home/$useName/Videos"
            },
            actual = AppDirs.getVideosDir(appName = null),
        )
    }
}