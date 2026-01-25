package com.github.panpf.sketch.core.desktop.test.util

import com.github.panpf.sketch.util.getComposeResourcesPath
import com.github.panpf.sketch.util.getJarPath
import okio.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        val path = getJarPath(JvmUtilsTest::class.java).orEmpty()
        val expectPart = arrayOf(
            "sketch-core",
            "build",
            "classes",
            "kotlin",
            "desktop",
            "test"
        ).joinToString(separator = Path.DIRECTORY_SEPARATOR, prefix = Path.DIRECTORY_SEPARATOR)
        assertTrue(actual = path.contains(expectPart), message = "path: $path")
    }
}