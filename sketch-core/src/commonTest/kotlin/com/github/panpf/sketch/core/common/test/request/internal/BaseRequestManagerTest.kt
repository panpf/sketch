package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.internal.BaseRequestManager
import com.github.panpf.sketch.request.internal.OneShotRequestDelegate
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.singleton.request.execute
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BaseRequestManagerTest {

    @Test
    fun testSetRequest() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val requestManager = object : BaseRequestManager() {
            override fun isAttached(): Boolean = true
        }
        val job1 = Job()
        val target1 = TestTarget()
        val requestDelegate1 = OneShotRequestDelegate(
            sketch = sketch,
            initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
            target = target1,
            job = job1
        )
        val job2 = Job()
        val target2 = TestTarget()
        val requestDelegate2 = OneShotRequestDelegate(
            sketch = sketch,
            initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
            target = target2,
            job = job2
        )

        assertTrue(job1.isActive)
        assertTrue(job2.isActive)
        assertFalse(target1.attached)
        assertFalse(target2.attached)

        requestManager.setRequest(requestDelegate1)
        assertTrue(job1.isActive)
        assertTrue(job2.isActive)
        assertTrue(target1.attached)
        assertFalse(target2.attached)

        requestManager.setRequest(requestDelegate2)
        assertFalse(job1.isActive)
        assertTrue(job2.isActive)
        assertTrue(target1.attached)
        assertTrue(target2.attached)

        requestManager.setRequest(null)
        assertFalse(job1.isActive)
        assertFalse(job2.isActive)
        assertTrue(target1.attached)
        assertTrue(target2.attached)
    }

    @Test
    fun testDisposeAndIsDisposedAndGetDisposable() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val requestManager = object : BaseRequestManager() {
            override fun isAttached(): Boolean = true
        }

        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val deferred1 = async {
            ImageResult.Error(request, null, Exception(""))
        }
        val deferred2 = async {
            ImageResult.Error(request, null, Exception(""))
        }
        val disposable1 = withContext(Dispatchers.Main) {
            requestManager.getDisposable(deferred1)
        }
        block(100)
        assertFalse(requestManager.isDisposed(disposable1))

        requestManager.isRestart = true
        assertTrue(requestManager.isRestart)
        assertNotSame(disposable1.job, deferred2.job)
        val disposable11 = withContext(Dispatchers.Main) {
            requestManager.getDisposable(deferred2)
        }
        block(100)
        assertFalse(requestManager.isRestart)
        assertSame(disposable1, disposable11)
        assertSame(disposable1.job, deferred2.job)

        requestManager.isRestart = false
        val disposable2 = withContext(Dispatchers.Main) {
            requestManager.getDisposable(deferred2)
        }
        block(100)
        assertNotSame(disposable1, disposable2)
        assertTrue(requestManager.isDisposed(disposable1))
        assertFalse(requestManager.isDisposed(disposable2))

        val job1 = Job()
        val target1 = TestTarget()
        val requestDelegate1 = OneShotRequestDelegate(
            sketch = sketch,
            initialRequest = request,
            target = target1,
            job = job1
        )
        requestManager.setRequest(requestDelegate1)
        assertNotNull(requestManager.getRequest())
        assertTrue(job1.isActive)
        assertFalse(requestManager.isDisposed(disposable2))
        withContext(Dispatchers.Main) {
            requestManager.dispose()
        }
        block(100)
        assertNull(requestManager.getRequest())
        assertFalse(job1.isActive)
        assertTrue(requestManager.isDisposed(disposable2))
    }

    @Test
    fun testGetResult() = runTest {
        val context = getTestContext()
        val requestManager = object : BaseRequestManager() {
            override fun isAttached(): Boolean = true
        }

        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val deferred1 = async(ioCoroutineDispatcher()) {
            block(1000)
            ImageResult.Error(request, null, Exception(""))
        }
        withContext(Dispatchers.Main) {
            requestManager.getDisposable(deferred1)
        }
        block(100)
        assertNull(requestManager.getResult())
        block(1000)
        assertTrue(requestManager.getResult() is ImageResult.Error)
    }

    @Test
    fun testRestart() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val target1 = TestTarget()
        val requestManager = target1.getRequestManager().asOrThrow<BaseRequestManager>()

        assertNull(target1.successImage)
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(Size.Origin)
            target(target1)
            memoryCachePolicy(CachePolicy.DISABLED)
            resultCachePolicy(CachePolicy.DISABLED)
            downloadCachePolicy(CachePolicy.DISABLED)
        }.execute(sketch)
        assertNotNull(target1.successImage)

        target1.successImage = null
        assertNull(target1.successImage)
        requestManager.restart()
        assertTrue(requestManager.isRestart)
        block(1000)
        assertTrue(requestManager.isRestart)
        assertNotNull(target1.successImage)
    }

    @Test
    fun testGetRequest() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val requestManager = object : BaseRequestManager() {
            override fun isAttached(): Boolean = true
        }
        val job1 = Job()
        val target1 = TestTarget()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val requestDelegate1 = OneShotRequestDelegate(
            sketch = sketch,
            initialRequest = request,
            target = target1,
            job = job1
        )

        assertNull(requestManager.getRequest())
        requestManager.setRequest(requestDelegate1)
        assertSame(request, requestManager.getRequest())
    }

    @Test
    fun testGetSketch() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val requestManager = object : BaseRequestManager() {
            override fun isAttached(): Boolean = true
        }
        val job1 = Job()
        val target1 = TestTarget()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val requestDelegate1 = OneShotRequestDelegate(
            sketch = sketch,
            initialRequest = request,
            target = target1,
            job = job1
        )

        assertNull(requestManager.getSketch())
        requestManager.setRequest(requestDelegate1)
        assertSame(sketch, requestManager.getSketch())
    }
}