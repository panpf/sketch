package com.github.panpf.sketch.core.macos.test.util

import com.github.panpf.sketch.util.AppDirs
import platform.Foundation.NSUserName
import kotlin.test.Test
import kotlin.test.assertEquals

class AppDirsMacosTest {

    private val appName = "SketchSample"
    private val useName = NSUserName()

    @Test
    fun testGetConfigDir() {
        assertEquals(
            expected = "/Users/$useName/Library/Preferences/$appName",
            actual = AppDirs.getConfigDir(appName = appName),
        )

        assertEquals(
            expected = "/Users/$useName/Library/Preferences",
            actual = AppDirs.getConfigDir(appName = null),
        )
    }

    @Test
    fun testGetDataDir() {
        assertEquals(
            expected = "/Users/$useName/Library/Application Support/$appName",
            actual = AppDirs.getDataDir(appName = appName),
        )

        assertEquals(
            expected = "/Users/$useName/Library/Application Support",
            actual = AppDirs.getDataDir(appName = null),
        )
    }

    @Test
    fun testGetCacheDir() {
        assertEquals(
            expected = "/Users/$useName/Library/Caches/$appName",
            actual = AppDirs.getCacheDir(appName = appName),
        )

        assertEquals(
            expected = "/Users/$useName/Library/Caches",
            actual = AppDirs.getCacheDir(appName = null),
        )
    }

    @Test
    fun testGetDesktopDir() {
        assertEquals(
            expected = "/Users/$useName/Desktop/$appName",
            actual = AppDirs.getDesktopDir(appName = appName),
        )

        assertEquals(
            expected = "/Users/$useName/Desktop",
            actual = AppDirs.getDesktopDir(appName = null),
        )
    }

    @Test
    fun testGetDocumentsDir() {
        assertEquals(
            expected = "/Users/$useName/Documents/$appName",
            actual = AppDirs.getDocumentsDir(appName = appName),
        )

        assertEquals(
            expected = "/Users/$useName/Documents",
            actual = AppDirs.getDocumentsDir(appName = null),
        )
    }

    @Test
    fun testGetDownloadsDir() {
        assertEquals(
            expected = "/Users/$useName/Downloads/$appName",
            actual = AppDirs.getDownloadsDir(appName = appName),
        )

        assertEquals(
            expected = "/Users/$useName/Downloads",
            actual = AppDirs.getDownloadsDir(appName = null),
        )
    }

    @Test
    fun testGetPicturesDir() {
        assertEquals(
            expected = "/Users/$useName/Pictures/$appName",
            actual = AppDirs.getPicturesDir(appName = appName),
        )

        assertEquals(
            expected = "/Users/$useName/Pictures",
            actual = AppDirs.getPicturesDir(appName = null),
        )
    }

    @Test
    fun testGetMusicDir() {
        assertEquals(
            expected = "/Users/$useName/Music/$appName",
            actual = AppDirs.getMusicDir(appName = appName),
        )

        assertEquals(
            expected = "/Users/$useName/Music",
            actual = AppDirs.getMusicDir(appName = null),
        )
    }

    @Test
    fun testGetVideosDir() {
        assertEquals(
            expected = "/Users/$useName/Movies/$appName",
            actual = AppDirs.getVideosDir(appName = appName),
        )

        assertEquals(
            expected = "/Users/$useName/Movies",
            actual = AppDirs.getVideosDir(appName = null),
        )
    }
}