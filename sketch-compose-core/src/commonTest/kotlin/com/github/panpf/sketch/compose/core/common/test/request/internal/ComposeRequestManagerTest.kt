package com.github.panpf.sketch.compose.core.common.test.request.internal

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ComposeRequestDelegate
import com.github.panpf.sketch.request.internal.ComposeRequestManager
import com.github.panpf.sketch.target.TestGenericComposeTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlinx.coroutines.Job
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeRequestManagerTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndSketch()
        val job = Job()
        val requestManager = ComposeRequestManager().apply {
            setRequest(
                ComposeRequestDelegate(
                    sketch = sketch,
                    initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
                    target = TestGenericComposeTarget(),
                    job = job
                )
            )
        }
        assertEquals(0, requestManager.rememberedCount)
        assertEquals(false, requestManager.isAttached())
        assertEquals(true, job.isActive)

        requestManager.onRemembered()
        assertEquals(1, requestManager.rememberedCount)
        assertEquals(true, requestManager.isAttached())
        assertEquals(true, job.isActive)

        requestManager.onRemembered()
        assertEquals(2, requestManager.rememberedCount)
        assertEquals(true, requestManager.isAttached())
        assertEquals(true, job.isActive)

        requestManager.onRemembered()
        assertEquals(3, requestManager.rememberedCount)
        assertEquals(true, requestManager.isAttached())
        assertEquals(true, job.isActive)

        requestManager.onForgotten()
        assertEquals(2, requestManager.rememberedCount)
        assertEquals(true, requestManager.isAttached())
        assertEquals(true, job.isActive)

        requestManager.onForgotten()
        assertEquals(1, requestManager.rememberedCount)
        assertEquals(true, requestManager.isAttached())
        assertEquals(true, job.isActive)

        requestManager.onForgotten()
        assertEquals(0, requestManager.rememberedCount)
        assertEquals(false, requestManager.isAttached())
        assertEquals(false, job.isActive)

        requestManager.onForgotten()
        assertEquals(0, requestManager.rememberedCount)
        assertEquals(false, requestManager.isAttached())
        assertEquals(false, job.isActive)
    }
}