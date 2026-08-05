package com.github.panpf.sketch.core.apple.test.util

import com.github.panpf.sketch.util.defaultFileSystem
import okio.FileSystem
import kotlin.test.Test
import kotlin.test.assertEquals

class FileSystemsAppleTest {

    @Test
    fun testDefaultFileSystem() {
        assertEquals(
            expected = FileSystem.SYSTEM,
            actual = defaultFileSystem(),
        )
    }
}
