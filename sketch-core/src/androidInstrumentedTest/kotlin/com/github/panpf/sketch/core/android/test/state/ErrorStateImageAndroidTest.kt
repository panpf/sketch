package com.github.panpf.sketch.core.android.test.state

import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.addState
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.UriInvalidCondition
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorStateImageAndroidTest {

    @Test
    fun testAddState() {
        assertEquals(
            expected = ErrorStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    UriInvalidCondition,
                    DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
                )
            },
            actual = ErrorStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    condition = UriInvalidCondition,
                    drawable = ColorDrawable(TestColor.RED).asEquitable()
                )
            }
        )

        assertEquals(
            expected = ErrorStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    UriInvalidCondition,
                    DrawableStateImage(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                )
            },
            actual = ErrorStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    condition = UriInvalidCondition,
                    resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated
                )
            }
        )

        assertEquals(
            expected = ErrorStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(UriInvalidCondition, ColorDrawableStateImage(IntColor(TestColor.RED)))
            },
            actual = ErrorStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    condition = UriInvalidCondition,
                    color = IntColor(TestColor.RED)
                )
            }
        )

        assertEquals(
            expected = ErrorStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    UriInvalidCondition,
                    ColorDrawableStateImage(ResColor(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated))
                )
            },
            actual = ErrorStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    condition = UriInvalidCondition,
                    color = ResColor(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                )
            }
        )
    }
}