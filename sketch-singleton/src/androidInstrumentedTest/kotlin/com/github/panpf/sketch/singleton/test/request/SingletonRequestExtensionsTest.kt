package com.github.panpf.sketch.singleton.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.enqueue
import com.github.panpf.sketch.request.execute
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SingletonRequestExtensionsTest {

    @Test
    fun testExecuteAndEnqueue() = runTest {
        val context = getTestContext()

        ImageRequest(context, MyImages.jpeg.uri)
            .execute()
            .apply {
                Assert.assertTrue(this is Success)
            }

        ImageRequest(context, MyImages.jpeg.uri)
            .enqueue().job.await()
            .apply {
                Assert.assertTrue(this is Success)
            }
    }
}