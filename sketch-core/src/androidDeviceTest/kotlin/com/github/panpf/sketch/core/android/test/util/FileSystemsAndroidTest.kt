package com.github.panpf.sketch.core.android.test.util

import com.github.panpf.sketch.util.defaultFileSystem
import okio.FileSystem
import kotlin.test.Test
import kotlin.test.assertEquals

class FileSystemsAndroidTest {

    @Test
    fun testDefaultFileSystem() {
        assertEquals(
            expected = FileSystem.SYSTEM,
            actual = defaultFileSystem(),
        )
    }
}