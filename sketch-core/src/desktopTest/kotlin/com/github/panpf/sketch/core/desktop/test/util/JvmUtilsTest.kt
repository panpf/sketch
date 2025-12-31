package com.github.panpf.sketch.core.desktop.test.util

import com.github.panpf.sketch.util.getComposeResourcesPath
import com.github.panpf.sketch.util.getJarPath
import okio.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmUtilsTest {

    @Test
    fun testGetComposeResourcesPath() {
        assertEquals(
            expected = null,
            actual = getComposeResourcesPath()
        )
    }

    @Test
    fun testGetJarPath() {
        assertEquals(
            expected = true,
            actual = getJarPath(JvmUtilsTest::class.java).orEmpty()
                .contains("${Path.DIRECTORY_SEPARATOR}sketch${Path.DIRECTORY_SEPARATOR}sketch-core${Path.DIRECTORY_SEPARATOR}build${Path.DIRECTORY_SEPARATOR}classes${Path.DIRECTORY_SEPARATOR}kotlin${Path.DIRECTORY_SEPARATOR}desktop${Path.DIRECTORY_SEPARATOR}test")
        )
    }
}