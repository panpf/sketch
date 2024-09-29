package com.github.panpf.sketch.extensions.core.android.test.drawable

import android.graphics.Color
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_SECTOR_SIZE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.internal.dp2Px
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class SectorProgressDrawableTest {

    @Test
    fun testConstructor() {
        SectorProgressDrawable(
            size = 101,
            backgroundColor = Color.GREEN,
            strokeColor = Color.BLACK,
            progressColor = Color.RED,
            strokeWidth = 2.02f,
            hiddenWhenIndeterminate = true,
            hiddenWhenCompleted = false,
            stepAnimationDuration = 150
        )

        SectorProgressDrawable(
            101, Color.GREEN, Color.BLACK, Color.RED, 2.02f, true, false, 150
        )
    }

    @Test
    fun testSize() {
        assertEquals(
            expected = Size(
                width = PROGRESS_INDICATOR_SECTOR_SIZE.dp2Px(),
                height = PROGRESS_INDICATOR_SECTOR_SIZE.dp2Px()
            ),
            actual = SectorProgressDrawable().intrinsicSize
        )
        assertEquals(
            expected = Size(101, 101),
            actual = SectorProgressDrawable(101).intrinsicSize
        )
    }

    @Test
    fun testMutable() {
        val drawable = SectorProgressDrawable(101)
        assertSame(
            expected = drawable,
            actual = drawable.mutate()
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val drawable1 = SectorProgressDrawable()
        val drawable11 = SectorProgressDrawable()
        val drawable2 = SectorProgressDrawable(size = 102)
        val drawable3 = SectorProgressDrawable(backgroundColor = Color.GREEN)
        val drawable4 = SectorProgressDrawable(strokeColor = Color.BLACK)
        val drawable5 = SectorProgressDrawable(progressColor = Color.RED)
        val drawable6 = SectorProgressDrawable(strokeWidth = 100f)
        val drawable7 = SectorProgressDrawable(
            hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
        )
        val drawable8 = SectorProgressDrawable(
            hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
        )
        val drawable9 = SectorProgressDrawable(
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

    @Test
    fun testToString() {
        assertEquals(
            expected = "SectorProgressDrawable(size=101, backgroundColor=1140850688, strokeColor=-1, progressColor=-1, strokeWidth=2.02, hiddenWhenIndeterminate=false, hiddenWhenCompleted=true, stepAnimationDuration=150)",
            actual = SectorProgressDrawable(size = 101).toString()
        )
    }
}