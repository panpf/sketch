package com.github.panpf.sketch.images.android.test

import com.github.panpf.sketch.images.AssetImageFiles
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AssetResImageFilesTest {

    @Test
    fun test() = runTest {
        val context = getTestContext()
        val imageFile = AssetImageFiles.bird
        val byteArray = imageFile.toDataSource(context).toByteArray()
        assertEquals(
            message = "byteArray size ${byteArray.size}",
            expected = imageFile.length,
            actual = byteArray.size.toLong()
        )
    }
}