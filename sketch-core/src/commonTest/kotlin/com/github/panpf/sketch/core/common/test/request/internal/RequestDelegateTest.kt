package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.NoTargetRequestDelegate
import com.github.panpf.sketch.request.internal.requestDelegate
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestTarget
import kotlinx.coroutines.Job
import kotlin.test.Test
import kotlin.test.assertTrue

class RequestDelegateTest {

    @Test
    fun testRequestDelegate() {
        val (context, sketch) = getTestContextAndSketch()
        assertTrue(
            actual = requestDelegate(
                sketch = sketch,
                initialRequest = ImageRequest(context, "http://test.com/test.jpg"),
                job = Job()
            ) is NoTargetRequestDelegate
        )
        assertTrue(
            actual = requestDelegate(
                sketch = sketch,
                initialRequest = ImageRequest(context, "http://test.com/test.jpg") {
                    target(TestTarget())
                },
                job = Job()
            ) !is NoTargetRequestDelegate
        )
    }

    @Test
    fun testBaseRequestDelegate() {
        // TODO test
    }

    @Test
    fun testNoTargetRequestDelegate() {
        val (context, sketch) = getTestContextAndSketch()
        NoTargetRequestDelegate(
            sketch = sketch,
            initialRequest = ImageRequest(context, "http://test.com/test.jpg"),
            job = Job()
        )
        // TODO Supplementary testing
    }

    @Test
    fun testOneShotRequestDelegate() {
        // TODO test
    }
}