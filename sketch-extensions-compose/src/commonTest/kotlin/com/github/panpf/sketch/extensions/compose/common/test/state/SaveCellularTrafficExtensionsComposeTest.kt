package com.github.panpf.sketch.extensions.compose.common.test.state

import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.ConditionStateImage
import com.github.panpf.sketch.state.SaveCellularTrafficCondition
import com.github.panpf.sketch.state.saveCellularTrafficError
import com.github.panpf.sketch.test.utils.FakeStateImage
import kotlin.test.Test
import kotlin.test.assertEquals

class SaveCellularTrafficExtensionsComposeTest {

    @Test
    fun testSaveCellularTrafficError() {
        ConditionStateImage(FakeStateImage()) {
            saveCellularTrafficError(Color.Red)
        }.apply {
            assertEquals(
                expected = ColorPainterStateImage(Color.Red),
                actual = stateList.find { it.first == SaveCellularTrafficCondition }!!.second
            )
        }
    }
}