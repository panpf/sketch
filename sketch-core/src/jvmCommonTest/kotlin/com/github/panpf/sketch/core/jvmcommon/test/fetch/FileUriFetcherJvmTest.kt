package com.github.panpf.sketch.core.jvmcommon.test.fetch

import com.github.panpf.sketch.fetch.newFileUri
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class FileUriFetcherJvmTest {

    @Test
    fun testNewFileUri() {
        assertEquals(
            expected = "file:///sdcard/sample.jpg",
            actual = newFileUri(File("/sdcard/sample.jpg"))
        )
        assertEquals(
            expected = "file:///sdcard1/sample1.jpg",
            actual = newFileUri(File("/sdcard1/sample1.jpg"))
        )
    }
}