package com.github.panpf.sketch.extensions.compose.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_BACKGROUND_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_PROGRESS_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_SIZE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_STROKE_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.painter.SectorProgressPainter
import com.github.panpf.sketch.painter.rememberSectorProgressPainter
import com.github.panpf.sketch.test.utils.dp2Px
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalTestApi::class)
class SectorProgressPainterTest {

    @Test
    fun testRememberSectorProgressPainter() = runComposeUiTest {
        setContent {
            val density = LocalDensity.current
            assertEquals(
                expected = SectorProgressPainter(
                    density = density,
                    size = PROGRESS_INDICATOR_SECTOR_SIZE.dp,
                    backgroundColor = Color(PROGRESS_INDICATOR_SECTOR_BACKGROUND_COLOR),
                    strokeColor = Color(PROGRESS_INDICATOR_SECTOR_STROKE_COLOR),
                    progressColor = Color(PROGRESS_INDICATOR_SECTOR_PROGRESS_COLOR),
                    strokeWidth = PROGRESS_INDICATOR_SECTOR_SIZE.dp * PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT,
                    hiddenWhenIndeterminate = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
                ),
                actual = rememberSectorProgressPainter()
            )

            assertEquals(
                expected = SectorProgressPainter(
                    density = density,
                    size = (PROGRESS_INDICATOR_SECTOR_SIZE + 1).dp,
                    backgroundColor = Color.Green,
                    strokeColor = Color.Blue,
                    progressColor = Color.Yellow,
                    strokeWidth = (PROGRESS_INDICATOR_SECTOR_SIZE + 1).dp * PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT,
                    hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                ),
                actual = rememberSectorProgressPainter(
                    (PROGRESS_INDICATOR_SECTOR_SIZE + 1).dp,
                    Color.Green,
                    Color.Blue,
                    Color.Yellow,
                    (PROGRESS_INDICATOR_SECTOR_SIZE + 1).dp * PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT,
                    !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                )
            )

            assertEquals(
                expected = SectorProgressPainter(
                    density = density,
                    size = (PROGRESS_INDICATOR_SECTOR_SIZE + 1).dp,
                    backgroundColor = Color.Green,
                    strokeColor = Color.Blue,
                    progressColor = Color.Yellow,
                    strokeWidth = (PROGRESS_INDICATOR_SECTOR_SIZE + 1).dp * PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT,
                    hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
                    hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
                    stepAnimationDuration = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION + 1
                ),
                actual = rememberSectorProgressPainter(
                    size = (PROGRESS_INDICATOR_SECTOR_SIZE + 1).dp,
                    backgroundColor = Color.Green,
                    strokeColor = Color.Blue,
                    progressColor = Color.Yellow,
                    strokeWidth = (PROGRESS_INDICATOR_SECTOR_SIZE + 1).dp * PROGRESS_INDICATOR_SECTOR_STROKE_WIDTH_PERCENT,
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
            SectorProgressPainter(
                density = density,
                size = 101.dp,
                backgroundColor = Color.Green,
                strokeColor = Color.Black,
                progressColor = Color.Red,
                strokeWidth = 2.02f.dp,
                hiddenWhenIndeterminate = true,
                hiddenWhenCompleted = false,
                stepAnimationDuration = 150
            )

            SectorProgressPainter(
                density, 101.dp, Color.Green, Color.Black, Color.Red, 2.02f.dp, true, false, 150
            )
        }
    }

    @Test
    fun testSize() = runComposeUiTest {
        setContent {
            assertEquals(
                expected = Size(
                    width = PROGRESS_INDICATOR_SECTOR_SIZE.dp2Px(),
                    height = PROGRESS_INDICATOR_SECTOR_SIZE.dp2Px()
                ),
                actual = SectorProgressPainter(density).intrinsicSize
            )
            assertEquals(
                expected = Size(101f, 101f),
                actual = SectorProgressPainter(density, 101.dp).intrinsicSize
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
            val drawable1 = SectorProgressPainter(density)
            val drawable11 = SectorProgressPainter(density)
            val drawable2 = SectorProgressPainter(density, size = 102.dp)
            val drawable3 = SectorProgressPainter(density, backgroundColor = Color.Green)
            val drawable4 = SectorProgressPainter(density, strokeColor = Color.Black)
            val drawable5 = SectorProgressPainter(density, progressColor = Color.Red)
            val drawable6 = SectorProgressPainter(density, strokeWidth = 100f.dp)
            val drawable7 = SectorProgressPainter(
                density = density,
                hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
            )
            val drawable8 = SectorProgressPainter(
                density = density,
                hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
            )
            val drawable9 = SectorProgressPainter(
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
            assertNotEquals(illegal = drawable1, actual = drawable9)
            assertNotEquals(illegal = drawable2, actual = drawable3)
            assertNotEquals(illegal = drawable2, actual = drawable4)
            assertNotEquals(illegal = drawable2, actual = drawable5)
            assertNotEquals(illegal = drawable2, actual = drawable6)
            assertNotEquals(illegal = drawable2, actual = drawable7)
            assertNotEquals(illegal = drawable2, actual = drawable8)
            assertNotEquals(illegal = drawable2, actual = drawable9)
            assertNotEquals(illegal = drawable3, actual = drawable4)
            assertNotEquals(illegal = drawable3, actual = drawable5)
            assertNotEquals(illegal = drawable3, actual = drawable6)
            assertNotEquals(illegal = drawable3, actual = drawable7)
            assertNotEquals(illegal = drawable3, actual = drawable8)
            assertNotEquals(illegal = drawable3, actual = drawable9)
            assertNotEquals(illegal = drawable4, actual = drawable5)
            assertNotEquals(illegal = drawable4, actual = drawable6)
            assertNotEquals(illegal = drawable4, actual = drawable7)
            assertNotEquals(illegal = drawable4, actual = drawable8)
            assertNotEquals(illegal = drawable4, actual = drawable9)
            assertNotEquals(illegal = drawable5, actual = drawable6)
            assertNotEquals(illegal = drawable5, actual = drawable7)
            assertNotEquals(illegal = drawable5, actual = drawable8)
            assertNotEquals(illegal = drawable5, actual = drawable9)
            assertNotEquals(illegal = drawable6, actual = drawable7)
            assertNotEquals(illegal = drawable6, actual = drawable8)
            assertNotEquals(illegal = drawable6, actual = drawable9)
            assertNotEquals(illegal = drawable7, actual = drawable8)
            assertNotEquals(illegal = drawable7, actual = drawable9)
            assertNotEquals(illegal = drawable8, actual = drawable9)
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
            assertNotEquals(illegal = drawable1.hashCode(), actual = drawable9.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable3.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable4.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable5.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable6.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable2.hashCode(), actual = drawable9.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable4.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable5.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable6.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable3.hashCode(), actual = drawable9.hashCode())
            assertNotEquals(illegal = drawable4.hashCode(), actual = drawable5.hashCode())
            assertNotEquals(illegal = drawable4.hashCode(), actual = drawable6.hashCode())
            assertNotEquals(illegal = drawable4.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable4.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable4.hashCode(), actual = drawable9.hashCode())
            assertNotEquals(illegal = drawable5.hashCode(), actual = drawable6.hashCode())
            assertNotEquals(illegal = drawable5.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable5.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable5.hashCode(), actual = drawable9.hashCode())
            assertNotEquals(illegal = drawable6.hashCode(), actual = drawable7.hashCode())
            assertNotEquals(illegal = drawable6.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable6.hashCode(), actual = drawable9.hashCode())
            assertNotEquals(illegal = drawable7.hashCode(), actual = drawable8.hashCode())
            assertNotEquals(illegal = drawable7.hashCode(), actual = drawable9.hashCode())
            assertNotEquals(illegal = drawable8.hashCode(), actual = drawable9.hashCode())
        }
    }

    @Test
    fun testToString() = runComposeUiTest {
        setContent {
            val density = LocalDensity.current
            assertEquals(
                expected = "SectorProgressPainter(size=101.0.dp, backgroundColor=1140850688, strokeColor=-1, progressColor=-1, strokeWidth=2.02.dp, hiddenWhenIndeterminate=false, hiddenWhenCompleted=true, stepAnimationDuration=150)",
                actual = SectorProgressPainter(density, size = 101.dp).toString()
            )
        }
    }
}