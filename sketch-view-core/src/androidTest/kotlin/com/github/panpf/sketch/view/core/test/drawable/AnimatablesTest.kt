package com.github.panpf.sketch.view.core.test.drawable

import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.drawable.startWithLifecycle
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnimatablesTest {

    @Test
    fun testStartWithLifecycle() = runTest {
        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val animatableDrawable = TestAnimatableDrawable()
            val imageView = ImageView(activity)
            withContext(Dispatchers.Main) {
                imageView.setImageDrawable(animatableDrawable)
                activity.setContentView(imageView, LayoutParams(500, 500))
            }

            assertFalse(animatableDrawable.isRunning)

            val testLifecycle = TestLifecycle()
            assertEquals(
                expected = Lifecycle.State.INITIALIZED,
                actual = testLifecycle.currentState
            )

            animatableDrawable.startWithLifecycle(testLifecycle)
            assertFalse(animatableDrawable.isRunning)

            withContext(Dispatchers.Main) {
                testLifecycle.currentState = Lifecycle.State.CREATED
            }
            block(100)
            assertEquals(
                expected = Lifecycle.State.CREATED,
                actual = testLifecycle.currentState
            )
            assertFalse(animatableDrawable.isRunning)

            withContext(Dispatchers.Main) {
                testLifecycle.currentState = Lifecycle.State.STARTED
            }
            block(100)
            assertEquals(
                expected = Lifecycle.State.STARTED,
                actual = testLifecycle.currentState
            )
            assertTrue(animatableDrawable.isRunning)

            withContext(Dispatchers.Main) {
                testLifecycle.currentState = Lifecycle.State.RESUMED
            }
            block(100)
            assertEquals(
                expected = Lifecycle.State.RESUMED,
                actual = testLifecycle.currentState
            )
            assertTrue(animatableDrawable.isRunning)

            withContext(Dispatchers.Main) {
                testLifecycle.currentState = Lifecycle.State.STARTED
            }
            block(100)
            assertEquals(
                expected = Lifecycle.State.STARTED,
                actual = testLifecycle.currentState
            )
            assertTrue(animatableDrawable.isRunning)

            withContext(Dispatchers.Main) {
                testLifecycle.currentState = Lifecycle.State.CREATED
            }
            block(100)
            assertEquals(
                expected = Lifecycle.State.CREATED,
                actual = testLifecycle.currentState
            )
            assertFalse(animatableDrawable.isRunning)
        }
    }

    @Test
    fun testStartWithLifecycle2() = runTest {
        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val animatableDrawable = TestAnimatableDrawable()
            val imageView = ImageView(activity)
            withContext(Dispatchers.Main) {
                imageView.setImageDrawable(animatableDrawable)
                activity.setContentView(imageView, LayoutParams(500, 500))
            }

            assertFalse(animatableDrawable.isRunning)

            val testLifecycle = TestLifecycle()
            assertEquals(
                expected = Lifecycle.State.INITIALIZED,
                actual = testLifecycle.currentState
            )

            withContext(Dispatchers.Main) {
                testLifecycle.currentState = Lifecycle.State.CREATED
                testLifecycle.currentState = Lifecycle.State.STARTED
                testLifecycle.currentState = Lifecycle.State.RESUMED
            }
            block(100)
            assertEquals(
                expected = Lifecycle.State.RESUMED,
                actual = testLifecycle.currentState
            )

            animatableDrawable.startWithLifecycle(testLifecycle)
            assertTrue(animatableDrawable.isRunning)
        }
    }
}