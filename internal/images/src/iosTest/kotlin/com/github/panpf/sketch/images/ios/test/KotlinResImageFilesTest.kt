package com.github.panpf.sketch.images.ios.test

import com.github.panpf.sketch.images.KotlinResImageFiles
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinResImageFilesTest {

    @Test
    fun test() = runTest {
        val context = getTestContext()
        val imageFile = KotlinResImageFiles.bear
        val byteArray = imageFile.toDataSource(context).toByteArray()
        assertEquals(
            message = "byteArray size ${byteArray.size}",
            expected = imageFile.length,
            actual = byteArray.size.toLong()
        )
    }
}