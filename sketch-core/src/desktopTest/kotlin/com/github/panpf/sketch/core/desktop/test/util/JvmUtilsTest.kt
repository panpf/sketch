package com.github.panpf.sketch.core.desktop.test.util

import com.github.panpf.sketch.util.getComposeResourcesPath
import com.github.panpf.sketch.util.getJarPath
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
                .contains("/sketch/sketch-core/build/classes/kotlin/desktop/test")
        )
    }
}