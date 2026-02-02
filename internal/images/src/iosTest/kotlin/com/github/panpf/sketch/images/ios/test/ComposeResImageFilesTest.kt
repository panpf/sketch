package com.github.panpf.sketch.images.ios.test

import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.test.runTest
import okio.buffer
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeResImageFilesTest {

    @Test
    fun test() = runTest {
        val context = getTestContext()
        val imageFile = ComposeResImageFiles.jpeg
        val byteArray = imageFile.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
        assertEquals(
            message = "byteArray size ${byteArray.size}",
            expected = imageFile.length,
            actual = byteArray.size.toLong()
        )
    }
}