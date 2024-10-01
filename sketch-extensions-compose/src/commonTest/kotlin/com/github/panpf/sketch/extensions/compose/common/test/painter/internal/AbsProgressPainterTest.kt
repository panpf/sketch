@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.extensions.compose.common.test.painter.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.painter.internal.AbsProgressPainter
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import com.github.panpf.sketch.util.toSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AbsProgressPainterTest {

    @Test
    fun testProgress() = runComposeUiTest {
        setContent {
            val testProgressPainter = remember {
                TestProgressPainter(
                    size = Size(100, 100),
                    hiddenWhenIndeterminate = false,
                    hiddenWhenCompleted = true,
                    stepAnimationDuration = 150,
                )
            }
            Image(testProgressPainter, "image", Modifier.size(100.dp))
            LaunchedEffect(ioCoroutineDispatcher()) {
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = -0.25f
                }
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = 0.25f
                }
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = 0.5f
                }
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = 0.75f
                }
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = 1f
                }
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = 1.25f
                }
                block(200)

                val actions = testProgressPainter.drawProgressHistory
                checkElements(actions)
                assertTrue(
                    actual = actions.first().toFloat() >= 0.0f,
                    message = "$actions"
                )
                assertEquals(
                    expected = "1.0",
                    actual = actions.last(),
                    message = "$actions"
                )
                assertTrue(actions.size >= 20)
            }
        }
    }

    @Test
    fun testProgress2() = runComposeUiTest {
        setContent {
            val testProgressPainter = remember {
                TestProgressPainter(
                    size = Size(100, 100),
                    hiddenWhenIndeterminate = true,
                    hiddenWhenCompleted = false,
                    stepAnimationDuration = 150,
                )
            }
            Image(testProgressPainter, "image", Modifier.size(100.dp))
            LaunchedEffect(ioCoroutineDispatcher()) {
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = -0.25f
                }
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = 0.25f
                }
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = 0.5f
                }
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = 0.75f
                }
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = 1f
                }
                block(200)

                withContext(Dispatchers.Main) {
                    testProgressPainter.progress = 1.25f
                }
                block(200)

                val actions = testProgressPainter.drawProgressHistory
                checkElements(actions)
                assertTrue(
                    actual = actions.first().toFloat() > 0.0f,
                    message = "$actions"
                )
                assertEquals(
                    expected = "1.0",
                    actual = actions.last(),
                    message = "$actions"
                )
                assertTrue(actions.size >= 20)
            }
        }
    }

    private fun checkElements(it: List<String>) {
        it.forEachIndexed { index, _ ->
            if (index > 0) {
                assertTrue(
                    actual = it[index].toFloat() >= it[index - 1].toFloat(),
                    message = "index: $index, $it"
                )
            }
        }
    }

    class TestProgressPainter(
        val size: Size,
        hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
        hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
        stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
    ) : AbsProgressPainter(
        hiddenWhenIndeterminate = hiddenWhenIndeterminate,
        hiddenWhenCompleted = hiddenWhenCompleted,
        stepAnimationDuration = stepAnimationDuration
    ) {

        val drawProgressHistory = mutableListOf<String>()

        override fun DrawScope.drawProgress(drawProgress: Float) {
            drawProgressHistory.add(drawProgress.format(2).toString())
        }

        override val intrinsicSize: androidx.compose.ui.geometry.Size
            get() = size.toSize()
    }
}