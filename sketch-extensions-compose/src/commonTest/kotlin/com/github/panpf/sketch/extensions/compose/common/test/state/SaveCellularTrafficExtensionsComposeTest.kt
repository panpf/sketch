package com.github.panpf.sketch.extensions.compose.common.test.state

import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.SaveCellularTrafficCondition
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.state.saveCellularTrafficError
import kotlin.test.Test
import kotlin.test.assertEquals

class SaveCellularTrafficExtensionsComposeTest {

    @Test
    fun testSaveCellularTrafficError() {
        ErrorStateImage(null as StateImage?) {
            saveCellularTrafficError(Color.Red)
        }.apply {
            assertEquals(
                expected = ColorPainterStateImage(Color.Red),
                actual = stateList.find { it.first == SaveCellularTrafficCondition }!!.second
            )
        }
    }
}