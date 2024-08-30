package com.github.panpf.sketch.core.android.test.android

import android.net.Uri
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidUriTest {

    @Test
    fun test() {
        val file = File("/sdcard/sample s.jpeg")
        assertEquals(
            expected = "file:///sdcard/sample%20s.jpeg",
            actual = Uri.fromFile(file).toString()
        )
    }
}