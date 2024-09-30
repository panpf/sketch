package com.github.panpf.sketch.core.common.test.request.internal

import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.BaseRequestDelegate
import com.github.panpf.sketch.request.internal.NoTargetRequestDelegate
import com.github.panpf.sketch.request.internal.OneShotRequestDelegate
import com.github.panpf.sketch.request.internal.requestDelegate
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.runBlock
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
    fun testBaseRequestDelegate() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        // start, dispose, have target
        runBlock {
            val target = TestTarget()
            val job = Job()
            val delegate = object : BaseRequestDelegate(
                sketch = sketch,
                initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
                target = target,
                job = job
            ) {
                override fun assertActive() {

                }

                override fun finish() {
                }
            }

            val lifecycle = TestLifecycle().apply {
                currentState = Lifecycle.State.RESUMED
            }
            assertNull(lifecycle.observers.find { it === delegate })
            assertNull(lifecycle.observers.find { it === target })
            assertNull(target.getRequestManager().getRequest())

            delegate.start(lifecycle)
            block(100)
            assertNotNull(lifecycle.observers.find { it === delegate })
            assertNotNull(lifecycle.observers.find { it === target })
            assertNotNull(target.getRequestManager().getRequest())

            // dispose
            assertTrue(job.isActive)
            delegate.dispose()
            assertFalse(job.isActive)
            assertNull(lifecycle.observers.find { it === delegate })
            assertNull(lifecycle.observers.find { it === target })
            assertNotNull(target.getRequestManager().getRequest())
        }

        // start, dispose, no target
        runBlock {
            val job = Job()
            val delegate = object : BaseRequestDelegate(
                sketch = sketch,
                initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
                target = null,
                job = job
            ) {
                override fun assertActive() {

                }

                override fun finish() {
                }
            }

            val lifecycle = TestLifecycle().apply {
                currentState = Lifecycle.State.RESUMED
            }
            assertNull(lifecycle.observers.find { it === delegate })

            delegate.start(lifecycle)
            block(100)
            assertNotNull(lifecycle.observers.find { it === delegate })

            // dispose
            assertTrue(job.isActive)
            delegate.dispose()
            assertFalse(job.isActive)
            assertNull(lifecycle.observers.find { it === delegate })
        }

        // onStateChanged, have target
        runBlock {
            val target = TestTarget()
            val job = Job()
            val delegate = object : BaseRequestDelegate(
                sketch = sketch,
                initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
                target = target,
                job = job
            ) {
                override fun assertActive() {

                }

                override fun finish() {
                }
            }

            val lifecycle = TestLifecycle().apply {
                currentState = Lifecycle.State.RESUMED
            }
            assertNull(lifecycle.observers.find { it === delegate })
            assertNull(lifecycle.observers.find { it === target })
            assertNull(target.getRequestManager().getRequest())

            delegate.start(lifecycle)
            block(100)
            assertNotNull(lifecycle.observers.find { it === delegate })
            assertNotNull(lifecycle.observers.find { it === target })
            assertNotNull(target.getRequestManager().getRequest())

            // lifecycle destroy
            assertTrue(job.isActive)
            lifecycle.currentState = Lifecycle.State.DESTROYED
            block(100)
            assertFalse(job.isActive)
            assertNull(lifecycle.observers.find { it === delegate })
            assertNull(lifecycle.observers.find { it === target })
            assertNull(target.getRequestManager().getRequest())
        }

        // onStateChanged, no target
        runBlock {
            val job = Job()
            val delegate = object : BaseRequestDelegate(
                sketch = sketch,
                initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
                target = null,
                job = job
            ) {
                override fun assertActive() {

                }

                override fun finish() {
                }
            }

            val lifecycle = TestLifecycle().apply {
                currentState = Lifecycle.State.RESUMED
            }
            assertNull(lifecycle.observers.find { it === delegate })

            delegate.start(lifecycle)
            block(100)
            assertNotNull(lifecycle.observers.find { it === delegate })

            // lifecycle destroy
            assertTrue(job.isActive)
            lifecycle.currentState = Lifecycle.State.DESTROYED
            block(100)
            assertFalse(job.isActive)
            assertNull(lifecycle.observers.find { it === delegate })
        }

        // onAttachedChanged
        runBlock {
            val target = TestTarget()
            val job = Job()
            val delegate = object : BaseRequestDelegate(
                sketch = sketch,
                initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
                target = target,
                job = job
            ) {
                override fun assertActive() {

                }

                override fun finish() {
                }
            }

            assertFalse(target.attached)

            delegate.onAttachedChanged(true)
            assertTrue(target.attached)

            delegate.onAttachedChanged(false)
            assertFalse(target.attached)
        }
    }

    @Test
    fun testNoTargetRequestDelegate() {
        val (context, sketch) = getTestContextAndSketch()
        val delegate = NoTargetRequestDelegate(
            sketch = sketch,
            initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
            job = Job()
        )

        // assertActive
        delegate.assertActive()

        // lifecycle
        val lifecycle = TestLifecycle().apply {
            currentState = Lifecycle.State.RESUMED
        }
        assertNull(lifecycle.observers.find { it === delegate })

        delegate.start(lifecycle)
        block(100)
        assertNotNull(lifecycle.observers.find { it === delegate })

        delegate.finish()
        block(100)
        assertNull(lifecycle.observers.find { it === delegate })
    }

    @Test
    fun testOneShotRequestDelegate() {
        val (context, sketch) = getTestContextAndSketch()
        val viewTarget = TestTarget()
        val delegate = OneShotRequestDelegate(
            sketch = sketch,
            initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
            target = viewTarget,
            job = Job()
        )

        // assertActive
        assertNull(viewTarget.getRequestManager().getRequest())
        delegate.assertActive()
        assertNull(viewTarget.getRequestManager().getRequest())

        // lifecycle
        val lifecycle = TestLifecycle().apply {
            currentState = Lifecycle.State.RESUMED
        }
        assertNull(lifecycle.observers.find { it === delegate })
        assertNull(lifecycle.observers.find { it === viewTarget })

        delegate.start(lifecycle)
        block(100)
        assertNotNull(lifecycle.observers.find { it === delegate })
        assertNotNull(lifecycle.observers.find { it === viewTarget })

        delegate.finish()
        block(100)
        assertNull(lifecycle.observers.find { it === delegate })
        assertNull(lifecycle.observers.find { it === viewTarget })
    }
}