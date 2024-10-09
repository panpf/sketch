@file:OptIn(ExperimentalTestApi::class, ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.windowContainerSize
import kotlin.test.Test
import kotlin.test.assertEquals

class AsyncImageStateTest {

    @Test
    fun testRememberAsyncImageState() {
        val testLifecycle = TestLifecycle()
        runComposeUiTest {
            setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides testLifecycle.owner) {
                    rememberAsyncImageState().apply {
                        assertEquals(expected = testLifecycle, actual = lifecycle)
                        assertEquals(expected = false, actual = inspectionMode)
                        assertEquals(expected = windowContainerSize(), actual = containerSize)
                        assertEquals(expected = null, actual = options)
                    }

                    CompositionLocalProvider(LocalInspectionMode provides true) {
                        rememberAsyncImageState().apply {
                            assertEquals(expected = GlobalLifecycle, actual = lifecycle)
                            assertEquals(expected = true, actual = inspectionMode)
                            assertEquals(expected = windowContainerSize(), actual = containerSize)
                            assertEquals(expected = null, actual = options)
                        }
                    }

                    rememberAsyncImageState(ImageOptions { size(101, 202) }).apply {
                        assertEquals(expected = testLifecycle, actual = lifecycle)
                        assertEquals(expected = false, actual = inspectionMode)
                        assertEquals(expected = windowContainerSize(), actual = containerSize)
                        assertEquals(expected = ImageOptions { size(101, 202) }, actual = options)
                    }

                    rememberAsyncImageState {
                        ImageOptions { size(202, 101) }
                    }.apply {
                        assertEquals(expected = testLifecycle, actual = lifecycle)
                        assertEquals(expected = false, actual = inspectionMode)
                        assertEquals(expected = windowContainerSize(), actual = containerSize)
                        assertEquals(expected = ImageOptions { size(202, 101) }, actual = options)
                    }
                }
            }
        }
    }

    // TODO test
}