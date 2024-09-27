package com.github.panpf.sketch.core.android.test.state

import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.state.CurrentStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.test.utils.TestColor
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrentStateImageAndroidTest {

    @Test
    fun testCurrentStateImage() {
        assertEquals(
            expected = CurrentStateImage(DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())),
            actual = CurrentStateImage(ColorDrawable(TestColor.RED).asEquitable())
        )

        assertEquals(
            expected = CurrentStateImage(DrawableStateImage(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)),
            actual = CurrentStateImage(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
        )
    }
}