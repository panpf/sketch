package com.github.panpf.sketch.extensions.compose.common.test.painter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_MASK_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.painter.MaskProgressPainter
import com.github.panpf.sketch.painter.rememberMaskProgressPainter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalTestApi::class)
class MaskProgressPainterTest {

    @Test
    fun testRememberMaskProgressPainter() = runComposeUiTest {
        setContent {
            assertEquals(
                expected = MaskProgressPainter(
                    maskColor = Color(PROGRESS_INDICATOR_MASK_COLOR),
                    hiddenWhenIndeterminate = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
                ),
                actual = rememberMaskProgressPainter()
            )

            assertEquals(
                expected = MaskProgressPainter(
                    maskColor = Color.Green,
                    hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                ),
                actual = rememberMaskProgressPainter(
                    Color.Green,
                    !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                )
            )

            assertEquals(
                expected = MaskProgressPainter(
                    maskColor = Color.Green,
                    hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                ),
                actual = rememberMaskProgressPainter(
                    maskColor = Color.Green,
                    hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                )
            )
        }
    }

    @Test
    fun testConstructor() {
        MaskProgressPainter(
            maskColor = Color.Red,
            hiddenWhenIndeterminate = true,
            hiddenWhenCompleted = false,
            stepAnimationDuration = 150
        )

        MaskProgressPainter(
            Color.Red, true, false, 150
        )
    }

    @Test
    fun testSize() {
        assertEquals(
            expected = androidx.compose.ui.geometry.Size.Unspecified,
            actual = MaskProgressPainter().intrinsicSize
        )
    }

    @Test
    fun testDraw() {
        // TODO test: Draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testEqualsAndHashCode() {
        val drawable1 = MaskProgressPainter()
        val drawable11 = MaskProgressPainter()
        val drawable2 = MaskProgressPainter(maskColor = Color.Red)
        val drawable3 = MaskProgressPainter(
            hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
        )
        val drawable4 = MaskProgressPainter(
            hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
        )
        val drawable5 = MaskProgressPainter(
            stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION * 2
        )

        assertEquals(expected = drawable1, actual = drawable11)
        assertNotEquals(illegal = drawable1, actual = drawable2)
        assertNotEquals(illegal = drawable1, actual = drawable3)
        assertNotEquals(illegal = drawable1, actual = drawable4)
        assertNotEquals(illegal = drawable1, actual = drawable5)
        assertNotEquals(illegal = drawable2, actual = drawable3)
        assertNotEquals(illegal = drawable2, actual = drawable4)
        assertNotEquals(illegal = drawable2, actual = drawable5)
        assertNotEquals(illegal = drawable3, actual = drawable4)
        assertNotEquals(illegal = drawable3, actual = drawable5)
        assertNotEquals(illegal = drawable4, actual = drawable5)
        assertNotEquals(illegal = drawable1, actual = null as Any?)
        assertNotEquals(illegal = drawable1, actual = Any())

        assertEquals(expected = drawable1.hashCode(), actual = drawable11.hashCode())
        assertNotEquals(illegal = drawable1.hashCode(), actual = drawable2.hashCode())
        assertNotEquals(illegal = drawable1.hashCode(), actual = drawable3.hashCode())
        assertNotEquals(illegal = drawable1.hashCode(), actual = drawable4.hashCode())
        assertNotEquals(illegal = drawable1.hashCode(), actual = drawable5.hashCode())
        assertNotEquals(illegal = drawable2.hashCode(), actual = drawable3.hashCode())
        assertNotEquals(illegal = drawable2.hashCode(), actual = drawable4.hashCode())
        assertNotEquals(illegal = drawable2.hashCode(), actual = drawable5.hashCode())
        assertNotEquals(illegal = drawable3.hashCode(), actual = drawable4.hashCode())
        assertNotEquals(illegal = drawable3.hashCode(), actual = drawable5.hashCode())
        assertNotEquals(illegal = drawable4.hashCode(), actual = drawable5.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "MaskProgressPainter(maskColor=570425344, hiddenWhenIndeterminate=false, hiddenWhenCompleted=true, stepAnimationDuration=150)",
            actual = MaskProgressPainter().toString()
        )
    }
}