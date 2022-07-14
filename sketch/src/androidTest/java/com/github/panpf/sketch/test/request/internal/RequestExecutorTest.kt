@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.test.request.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.internal.RequestExecutor
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestExecutorTest {

    @Test
    fun testErrorUri() {
        val (context, sketch) = getTestContextAndNewSketch()

        runBlocking(Dispatchers.Main) {
            RequestExecutor().execute(
                sketch,
                LoadRequest(context, TestAssets.SAMPLE_JPEG_URI),
                false
            ).apply {
                Assert.assertTrue(this is LoadResult.Success)
            }

            RequestExecutor().execute(
                sketch,
                LoadRequest(context, ""),
                false
            ).apply {
                Assert.assertTrue(this is LoadResult.Error)
            }

            RequestExecutor().execute(
                sketch,
                LoadRequest(context, "  "),
                false
            ).apply {
                Assert.assertTrue(this is LoadResult.Error)
            }
        }
    }
}