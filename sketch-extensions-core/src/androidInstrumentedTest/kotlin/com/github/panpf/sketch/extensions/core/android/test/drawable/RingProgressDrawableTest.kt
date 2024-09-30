package com.github.panpf.sketch.extensions.core.android.test.drawable

import android.graphics.Color
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_SIZE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.drawable.RingProgressDrawable
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.dp2Px
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class RingProgressDrawableTest {

    @Test
    fun testConstructor() {
        RingProgressDrawable(
            size = 101,
            ringWidth = 2.02f,
            ringColor = Color.BLACK,
            backgroundColor = Color.GREEN,
            hiddenWhenIndeterminate = true,
            hiddenWhenCompleted = false,
            stepAnimationDuration = 150
        )
        RingProgressDrawable(
            101, 2.02f, Color.BLACK, Color.GREEN, true, false, 150
        )
    }

    @Test
    fun testSize() {
        assertEquals(
            expected = Size(
                width = PROGRESS_INDICATOR_RING_SIZE.dp2Px(),
                height = PROGRESS_INDICATOR_RING_SIZE.dp2Px()
            ),
            actual = RingProgressDrawable().intrinsicSize
        )
        assertEquals(
            expected = Size(101, 101),
            actual = RingProgressDrawable(101).intrinsicSize
        )
    }

    @Test
    fun testMutable() {
        val drawable = RingProgressDrawable(101)
        assertSame(
            expected = drawable,
            actual = drawable.mutate()
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val drawable1 = RingProgressDrawable()
        val drawable11 = RingProgressDrawable()
        val drawable2 = RingProgressDrawable(size = 102)
        val drawable3 = RingProgressDrawable(ringWidth = 100f)
        val drawable4 = RingProgressDrawable(ringColor = Color.BLACK)
        val drawable5 = RingProgressDrawable(backgroundColor = Color.GREEN)
        val drawable6 = RingProgressDrawable(
            hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
        )
        val drawable7 = RingProgressDrawable(
            hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
        )
        val drawable8 = RingProgressDrawable(
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

    @Test
    fun testToString() {
        assertEquals(
            expected = "RingProgressDrawable(size=101, ringWidth=10.1, ringColor=-1, backgroundColor=1090519039, hiddenWhenIndeterminate=false, hiddenWhenCompleted=true, stepAnimationDuration=150)",
            actual = RingProgressDrawable(size = 101).toString()
        )
    }
}