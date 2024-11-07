@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.panpf.sketch.painter.startWithLifecycle
import com.github.panpf.sketch.test.utils.TestAnimatablePainter
import com.github.panpf.sketch.test.utils.TestLifecycle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnimatablePainterTest {

    @Test
    fun testStartWithLifecycle() {
        runComposeUiTest {
            val lifecycle = TestLifecycle()
            val animatablePainter = TestAnimatablePainter(ColorPainter(Color.Cyan))
            setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides lifecycle.owner) {
                    assertEquals(Lifecycle.State.INITIALIZED, lifecycle.currentState)

                    assertFalse(animatablePainter.isRunning())
                    animatablePainter.startWithLifecycle()
                }
            }
            waitForIdle()

            assertFalse(animatablePainter.isRunning())
        }

        runComposeUiTest {
            val lifecycle = TestLifecycle()
            val animatablePainter = TestAnimatablePainter(ColorPainter(Color.Cyan))
            setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides lifecycle.owner) {
                    lifecycle.currentState = Lifecycle.State.CREATED
                    assertEquals(Lifecycle.State.CREATED, lifecycle.currentState)

                    assertFalse(animatablePainter.isRunning())
                    animatablePainter.startWithLifecycle()
                }
            }
            waitForIdle()

            assertFalse(animatablePainter.isRunning())
        }

        runComposeUiTest {
            val lifecycle = TestLifecycle()
            val animatablePainter = TestAnimatablePainter(ColorPainter(Color.Cyan))
            setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides lifecycle.owner) {
                    lifecycle.currentState = Lifecycle.State.STARTED
                    assertEquals(Lifecycle.State.STARTED, lifecycle.currentState)

                    assertFalse(animatablePainter.isRunning())
                    animatablePainter.startWithLifecycle()
                }
            }
            waitForIdle()

            assertTrue(animatablePainter.isRunning())
        }

        runComposeUiTest {
            val lifecycle = TestLifecycle()
            val animatablePainter = TestAnimatablePainter(ColorPainter(Color.Cyan))
            setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides lifecycle.owner) {
                    lifecycle.currentState = Lifecycle.State.STARTED
                    assertEquals(Lifecycle.State.STARTED, lifecycle.currentState)

                    assertFalse(animatablePainter.isRunning())
                    animatablePainter.startWithLifecycle()

                    lifecycle.currentState = Lifecycle.State.CREATED
                    assertEquals(Lifecycle.State.CREATED, lifecycle.currentState)
                }
            }
            waitForIdle()

            assertFalse(animatablePainter.isRunning())
        }

        runComposeUiTest {
            val lifecycle = TestLifecycle()
            val animatablePainter = TestAnimatablePainter(ColorPainter(Color.Cyan))
            setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides lifecycle.owner) {
                    lifecycle.currentState = Lifecycle.State.STARTED
                    assertEquals(Lifecycle.State.STARTED, lifecycle.currentState)

                    assertFalse(animatablePainter.isRunning())
                    animatablePainter.startWithLifecycle()
                }
            }
            waitForIdle()

            assertTrue(animatablePainter.isRunning())
            assertEquals(1, lifecycle.observers.size)
        }
    }
}