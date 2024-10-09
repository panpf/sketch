@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.request.internal

import androidx.compose.runtime.remember
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ComposeRequestDelegate
import com.github.panpf.sketch.request.internal.ComposeRequestManager
import com.github.panpf.sketch.target.TestGenericComposeTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestLifecycle
import kotlinx.coroutines.Job
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComposeRequestManagerTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndSketch()
        val asyncImageState = AsyncImageState(false, TestLifecycle(), IntSize(1000, 2000), null)
        val job = Job()
        val requestDelegate = ComposeRequestDelegate(
            sketch = sketch,
            initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
            target = TestGenericComposeTarget(),
            job = job
        )
        val requestManager = ComposeRequestManager(asyncImageState).apply {
            setRequest(requestDelegate)
        }
        assertFalse(requestManager.isAttached())

        runComposeUiTest {
            setContent {
                remember {
                    asyncImageState
                }

                assertTrue(job.isActive)
                requestManager.onForgotten()
                assertFalse(job.isActive)
            }

            assertTrue(requestManager.isAttached())
        }
    }
}