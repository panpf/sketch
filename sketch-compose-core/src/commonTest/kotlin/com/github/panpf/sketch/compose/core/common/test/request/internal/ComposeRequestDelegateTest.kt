package com.github.panpf.sketch.compose.core.common.test.request.internal

import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ComposeRequestDelegate
import com.github.panpf.sketch.target.TestGenericComposeTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.block
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ComposeRequestDelegateTest {

    @Test
    fun testAssertActive() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val composeTarget = TestGenericComposeTarget()
        val delegate = ComposeRequestDelegate(
            sketch = sketch,
            initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
            target = composeTarget,
            job = Job()
        )

        // assertActive
        assertNull(composeTarget.getRequestManager().getRequest())
        delegate.assertActive()
        assertNull(composeTarget.getRequestManager().getRequest())
    }

    @Test
    fun testFinish() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val composeTarget = TestGenericComposeTarget()
        val delegate = ComposeRequestDelegate(
            sketch = sketch,
            initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
            target = composeTarget,
            job = Job()
        )

        // lifecycle
        val lifecycle = TestLifecycle().apply {
            currentState = Lifecycle.State.RESUMED
        }
        assertNull(lifecycle.observers.find { it === delegate })
        assertNull(lifecycle.observers.find { it === composeTarget })

        delegate.start(lifecycle)
        block(100)
        assertNotNull(lifecycle.observers.find { it === delegate })
        assertNotNull(lifecycle.observers.find { it === composeTarget })

        delegate.finish()
        block(100)
        assertNotNull(lifecycle.observers.find { it === delegate })
        assertNotNull(lifecycle.observers.find { it === composeTarget })
    }
}