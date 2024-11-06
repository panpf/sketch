package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.request.internal.OneShotRequestManager
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DisposableTest {

    @Test
    fun testReusableDisposable() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        val context = getTestContext()
        val job = async(ioCoroutineDispatcher()) {
            block(1000)
            ImageResult.Error(
                request = ImageRequest(context = context, uri = ResourceImages.jpeg.uri),
                image = null,
                throwable = Exception("test")
            )
        }
        val requestManager = OneShotRequestManager()
        val disposable = withContext(Dispatchers.Main) {
            requestManager.getDisposable(job)
        }
        assertTrue(job.isActive)
        assertFalse(disposable.isDisposed)

        block(1500)
        assertFalse(job.isActive)
        assertFalse(disposable.isDisposed)

        requestManager.dispose()
        assertTrue(disposable.isDisposed)
    }

    @Test
    fun testOneShotDisposable() = runTest {
        val context = getTestContext()
        val job = async {
            block(1000)
            ImageResult.Error(
                ImageRequest(context, ResourceImages.jpeg.uri),
                null,
                Exception("test")
            )
        }

        val disposable = OneShotDisposable(job)
        assertFalse(disposable.isDisposed)

        block(500)
        assertFalse(disposable.isDisposed)
        disposable.dispose()

        block(1000)
        assertTrue(disposable.isDisposed)
        disposable.dispose()
    }
}