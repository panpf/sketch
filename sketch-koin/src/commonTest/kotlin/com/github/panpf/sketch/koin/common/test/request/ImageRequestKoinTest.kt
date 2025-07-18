package com.github.panpf.sketch.koin.common.test.request

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.enqueue
import com.github.panpf.sketch.request.execute
import com.github.panpf.sketch.test.utils.Koins
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ImageRequestKoinTest {

    init {
        Koins.initial()
    }

    @Test
    fun testEnqueue() = runTest {
        val context = getTestContext()

        ImageRequest(context, ResourceImages.jpeg.uri)
            .enqueue().job.await()
            .apply {
                assertTrue(this is Success)
            }
    }

    @Test
    fun testExecute() = runTest {
        val context = getTestContext()

        ImageRequest(context, ResourceImages.jpeg.uri)
            .execute()
            .apply {
                assertTrue(this is Success)
            }
    }
}