package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.asPainter
import com.github.panpf.sketch.state.asEquitablePainter
import kotlin.test.Test
import kotlin.test.assertEquals

class EquitableDrawableComposeAndroidTst {

    @Test
    fun testAsEquitablePainter() {
        ColorDrawable(Color.RED).asEquitablePainter(Color.RED).apply {
            assertEquals(
                expected = ColorDrawable(Color.RED).asPainter().asEquitable(Color.RED),
                actual = this
            )
        }

        ColorDrawable(Color.RED).asEquitable().asEquitablePainter().apply {
            assertEquals(
                expected = ColorDrawable(Color.RED).asEquitable().asPainter()
                    .asEquitable(ColorDrawable(Color.RED).asEquitable().equalityKey),
                actual = this
            )
        }
    }
}