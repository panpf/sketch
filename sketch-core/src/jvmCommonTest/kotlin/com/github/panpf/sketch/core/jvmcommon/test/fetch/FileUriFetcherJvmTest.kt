package com.github.panpf.sketch.core.jvmcommon.test.fetch

import com.github.panpf.sketch.fetch.newFileUri
import okio.Path
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class FileUriFetcherJvmTest {

    @Test
    fun testNewFileUri() {
        if (Path.DIRECTORY_SEPARATOR == "/") {
            assertEquals(
                expected = "file:///sdcard1/sample1.jpg",
                actual = newFileUri(File("/sdcard1/sample1.jpg"))
            )
        } else {
            assertEquals(
                expected = "D:\\test\\relative\\image.jpg",
                actual = newFileUri(File("D:\\test\\relative\\image.jpg"))
            )
        }
    }
}