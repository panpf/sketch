package com.github.panpf.sketch.view.core.test.request.internal

import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ViewRequestDelegate
import com.github.panpf.sketch.target.ImageViewTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ViewRequestDelegateTest {

    @Test
    fun testAssertActive() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        // No Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity)
            assertFalse(imageView.isAttachedToWindow)

            val viewTarget = ImageViewTarget(imageView)
            val delegate = ViewRequestDelegate(
                sketch = sketch,
                initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
                viewTarget = viewTarget,
                job = Job()
            )

            // assertActive
            assertNull(viewTarget.getRequestManager().getRequest())
            assertFailsWith(CancellationException::class) {
                delegate.assertActive()
            }
            assertNotNull(viewTarget.getRequestManager().getRequest())
        }

        // Attached
        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity)
            withContext(Dispatchers.Main) {
                activity.setContentView(imageView)
            }
            block(100)
            assertTrue(imageView.isAttachedToWindow)

            val viewTarget = ImageViewTarget(imageView)
            val delegate = ViewRequestDelegate(
                sketch = sketch,
                initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
                viewTarget = viewTarget,
                job = Job()
            )

            // assertActive
            assertNull(viewTarget.getRequestManager().getRequest())
            delegate.assertActive()
            assertNull(viewTarget.getRequestManager().getRequest())
        }
    }

    @Test
    fun testFinish() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        TestActivity::class.launchActivity().use { activityScenario ->
            val activity = activityScenario.getActivitySync()

            val imageView = ImageView(activity)
            withContext(Dispatchers.Main) {
                activity.setContentView(imageView)
            }
            block(100)
            assertTrue(imageView.isAttachedToWindow)

            val viewTarget = ImageViewTarget(imageView)
            val delegate = ViewRequestDelegate(
                sketch = sketch,
                initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
                viewTarget = viewTarget,
                job = Job()
            )

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
            assertNotNull(lifecycle.observers.find { it === delegate })
            assertNotNull(lifecycle.observers.find { it === viewTarget })
        }
    }
}