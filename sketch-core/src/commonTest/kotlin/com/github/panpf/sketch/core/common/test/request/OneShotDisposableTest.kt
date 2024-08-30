package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OneShotDisposableTest {

    @Test
    fun test() = runTest {
        val context = getTestContext()
        val job = async {
            delay(100)
            delay(100)
            delay(100)
            ImageResult.Error(
                ImageRequest(context, ResourceImages.jpeg.uri),
                null,
                Exception("test")
            )
        }
        val disposable = OneShotDisposable(job)
        assertFalse(disposable.isDisposed)
        delay(100)
        assertFalse(disposable.isDisposed)
        disposable.dispose()
        delay(100)
        assertTrue(disposable.isDisposed)
        disposable.dispose()
    }
}