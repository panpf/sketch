package com.github.panpf.sketch.extensions.compose.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_SIZE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_WIDTH_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.painter.RingProgressPainter
import com.github.panpf.sketch.painter.rememberRingProgressPainter
import com.github.panpf.sketch.test.utils.dp2Px
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalTestApi::class)
class RingProgressPainterTest {

    @Test
    fun testRememberRingProgressPainter() = runComposeUiTest {
        setContent {
            val density = LocalDensity.current
            assertEquals(
                expected = RingProgressPainter(
                    density = density,
                    size = PROGRESS_INDICATOR_RING_SIZE.dp,
                    ringWidth = PROGRESS_INDICATOR_RING_SIZE.dp * PROGRESS_INDICATOR_RING_WIDTH_PERCENT,
                    ringColor = Color(PROGRESS_INDICATOR_RING_COLOR),
                    backgroundColor = Color(PROGRESS_INDICATOR_RING_COLOR).copy(alpha = PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT),
                    hiddenWhenIndeterminate = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
                ),
                actual = rememberRingProgressPainter()
            )

            assertEquals(
                expected = RingProgressPainter(
                    density = density,
                    size = PROGRESS_INDICATOR_RING_SIZE.dp + 1.dp,
                    ringWidth = (PROGRESS_INDICATOR_RING_SIZE.dp + 1.dp) * PROGRESS_INDICATOR_RING_WIDTH_PERCENT,
                    ringColor = Color.Green,
                    backgroundColor = Color.Green.copy(alpha = PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT),
                    hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                ),
                actual = rememberRingProgressPainter(
                    PROGRESS_INDICATOR_RING_SIZE.dp + 1.dp,
                    (PROGRESS_INDICATOR_RING_SIZE.dp + 1.dp) * PROGRESS_INDICATOR_RING_WIDTH_PERCENT,
                    Color.Green,
                    Color.Green.copy(alpha = PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT),
                    !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                )
            )

            assertEquals(
                expected = RingProgressPainter(
                    density = density,
                    size = PROGRESS_INDICATOR_RING_SIZE.dp + 1.dp,
                    ringWidth = (PROGRESS_INDICATOR_RING_SIZE.dp + 1.dp) * PROGRESS_INDICATOR_RING_WIDTH_PERCENT,
                    ringColor = Color.Green,
                    backgroundColor = Color.Green.copy(alpha = PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT),
                    hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                ),
                actual = rememberRingProgressPainter(
                    size = PROGRESS_INDICATOR_RING_SIZE.dp + 1.dp,
                    ringWidth = (PROGRESS_INDICATOR_RING_SIZE.dp + 1.dp) * PROGRESS_INDICATOR_RING_WIDTH_PERCENT,
                    ringColor = Color.Green,
                    backgroundColor = Color.Green.copy(alpha = PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT),
                    hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                )
            )
        }
    }

    @Test
    fun testConstructor() = runComposeUiTest {
        setContent {
            val density = LocalDensity.current
            RingProgressPainter(
                density = density,
                size = 101.dp,
                ringWidth = 2.02.dp,
                ringColor = Color.Black,
                backgroundColor = Color.Green,
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150
            )
            RingProgressPainter(
                density, 101.dp, 2.02f.dp, Color.Black, Color.Green, true, false, 150
            )
        }
    }

    @Test
    fun testSize() = runComposeUiTest {
        setContent {
            val density = LocalDensity.current
            assertEquals(
                expected = Size(
                    width = PROGRESS_INDICATOR_RING_SIZE.dp2Px(),
                    height = PROGRESS_INDICATOR_RING_SIZE.dp2Px()
                ),
                actual = RingProgressPainter(density).intrinsicSize
            )
            assertEquals(
                expected = Size(101f, 101f),
                actual = RingProgressPainter(density, size = 101.dp).intrinsicSize
            )
        }
    }

    @Test
    fun testDraw() {
        // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testEqualsAndHashCode() = runComposeUiTest {
        setContent {
            val density = LocalDensity.current
            val drawable1 = RingProgressPainter(density)
            val drawable11 = RingProgressPainter(density)
            val drawable2 = RingProgressPainter(density, size = 102.dp)
            val drawable3 = RingProgressPainter(density, ringWidth = 100.dp)
            val drawable4 = RingProgressPainter(density, ringColor = Color.Black)
            val drawable5 = RingProgressPainter(density, backgroundColor = Color.Green)
            val drawable6 = RingProgressPainter(
                density = density,
                hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
            )
            val drawable7 = RingProgressPainter(
                density = density,
                hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
            )
            val drawable8 = RingProgressPainter(
                density = density,
                stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION * 2
            )

            assertEquals(expected = drawable1, actual = drawable11)
            assertNotEquals(illegal = drawable1, actual = drawable2)
            assertNotEquals(illegal = drawable1, actual = drawable3)
            assertNotEquals(illegal = drawable1, actual = drawable4)
            assertNotEquals(illegal = drawable1, actual = drawable5)
            assertNotEquals(illegal = drawable1, actual = drawable6)
            assertNotEquals(illegal = drawable1, actual = drawable7)
            assertNotEquals(illegal = drawable1, actual = drawable8)
            assertNotEquals(illegal = drawable2, actual = drawable3)
            assertNotEquals(illegal = drawable2, actual = drawable4)
            assertNotEquals(illegal = drawable2, actual = drawable5)
            assertNotEquals(illegal = drawable2, actual = drawable6)
            assertNotEquals(illegal = drawable2, actual = drawable7)
            assertNotEquals(illegal = drawable2, actual = drawable8)
            assertNotEquals(illegal = drawable3, actual = drawable4)
            assertNotEquals(illegal = drawable3, actual = drawable5)
            assertNotEquals(illegal = drawable3, actual = drawable6)
            assertNotEquals(illegal = drawable3, actual = drawable7)
            assertNotEquals(illegal = drawable3, actual = drawable8)
            assertNotEquals(illegal = drawable4, actual = drawable5)
            assertNotEquals(illegal = drawable4, actual = drawable6)
            assertNotEquals(illegal = drawable4, actual = drawable7)
            assertNotEquals(illegal = drawable4, actual = drawable8)
            assertNotEquals(illegal = drawable5, actual = drawable6)
            assertNotEquals(illegal = drawable5, actual = drawable7)
            assertNotEquals(illegal = drawable5, actual = drawable8)
            assertNotEquals(illegal = drawable6, actual = drawable7)
            assertNotEquals(illegal = drawable6, actual = drawable8)
            assertNotEquals(illegal = drawable7, actual = drawable8)
            assertNotEquals(illegal = drawable1, actual = null as Any?)
            assertNotEquals(illegal = drawable1, actual = Any())

            assertEquals(expected = drawable1.hashCode(), actual = drawable11.hashCode())
            assertNotEquals(illegal = drawable1.hashCode(), actual = drawable2.hashCode())
            assertNotEquals(illegal = drawable1.hashCode(), actual = drawable3.hashCode())
            assertNotEquals(illegal = drawable1.hashCode(), actual = drawable4.hashCode())
            assertNotEquals(illegal = drawable1.hashCode(), actual = drawable5.hashCode())
            assertNotEquals(illegal = drawable1.hashCode(), actual = drawable6.hashCode())
            assertNotEquals(illegal = drawable1.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable1.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable3.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable4.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable5.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable6.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable4.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable5.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable6.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable4.hashCode(), actual = drawable5.hashCode())
            assertNotEquals(illegal = drawable4.hashCode(), actual = drawable6.hashCode())
            assertNotEquals(illegal = drawable4.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable4.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable5.hashCode(), actual = drawable6.hashCode())
            assertNotEquals(illegal = drawable5.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable5.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable6.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable6.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable7.hashCode(), actual = drawable8.hashCode())
        }
    }

    @Test
    fun testToString() = runComposeUiTest {
        setContent {
            assertEquals(
                expected = "RingProgressPainter(size=101.0.dp, ringWidth=10.1.dp, ringColor=-1, backgroundColor=1090519039, hiddenWhenIndeterminate=false, hiddenWhenCompleted=true, stepAnimationDuration=150)",
                actual = RingProgressPainter(density, size = 101.dp).toString()
            )
        }
    }
}