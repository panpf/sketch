package com.github.panpf.sketch.test.request.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.test.getContextAndSketch
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.util.format
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestExecutorTest {

    // todo 测试各种属性对加载结果的影响

    @Test
    fun testResize() {
        val (context, sketch) = getContextAndSketch()
        val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)
        val result = runBlocking {
            sketch.execute(request)
        }
        val success = result as DisplayResult.Success
        Assert.assertEquals(
            (success.imageInfo.width / success.imageInfo.height.toFloat()).format(1),
            (success.drawable.intrinsicWidth / success.drawable.intrinsicHeight.toFloat()).format(1)
        )
    }
}