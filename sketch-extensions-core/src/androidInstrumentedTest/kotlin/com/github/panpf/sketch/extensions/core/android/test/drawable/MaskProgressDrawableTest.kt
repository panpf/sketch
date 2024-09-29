package com.github.panpf.sketch.extensions.core.android.test.drawable

import android.graphics.Color
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.drawable.MaskProgressDrawable
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class MaskProgressDrawableTest {

    @Test
    fun testConstructor() {
        MaskProgressDrawable(
            maskColor = Color.RED,
            hiddenWhenIndeterminate = true,
            hiddenWhenCompleted = false,
            stepAnimationDuration = 150
        )

        MaskProgressDrawable(
            Color.RED, true, false, 150
        )
    }

    @Test
    fun testSize() {
        assertEquals(
            expected = Size(-1, -1),
            actual = MaskProgressDrawable().intrinsicSize
        )
    }

    @Test
    fun testMutable() {
        val drawable = MaskProgressDrawable()
        assertSame(
            expected = drawable,
            actual = drawable.mutate()
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val drawable1 = MaskProgressDrawable()
        val drawable11 = MaskProgressDrawable()
        val drawable2 = MaskProgressDrawable(maskColor = Color.RED)
        val drawable3 = MaskProgressDrawable(
            hiddenWhenIndeterminate = !PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
        )
        val drawable4 = MaskProgressDrawable(
            hiddenWhenCompleted = !PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
        )
        val drawable5 = MaskProgressDrawable(
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
            expected = "MaskProgressDrawable(maskColor=570425344, hiddenWhenIndeterminate=false, hiddenWhenCompleted=true, stepAnimationDuration=150)",
            actual = MaskProgressDrawable().toString()
        )
    }
}