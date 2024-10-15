package com.github.panpf.sketch.extensions.compose.resources.common.test

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.rememberEquitablePainterResource
import com.github.panpf.sketch.state.ComposableConditionStateImage
import com.github.panpf.sketch.state.ConditionStateImage.DefaultCondition
import com.github.panpf.sketch.state.PainterStateImage
import com.github.panpf.sketch.state.SaveCellularTrafficCondition
import com.github.panpf.sketch.state.saveCellularTrafficError
import com.github.panpf.sketch.test.compose.resources.Res
import com.github.panpf.sketch.test.compose.resources.desert
import com.github.panpf.sketch.test.compose.resources.moon
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SaveCellularTrafficExtensionsComposeResourcesTest {

    @Test
    fun testSaveCellularTrafficError() {
        runComposeUiTest {
            setContent {
                ComposableConditionStateImage(Res.drawable.moon) {
                    saveCellularTrafficError(Res.drawable.desert)
                }.apply {
                    assertEquals(2, stateList.size)
                    assertEquals(
                        expected = PainterStateImage(rememberEquitablePainterResource(Res.drawable.moon)),
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                    assertEquals(
                        expected = PainterStateImage(rememberEquitablePainterResource(Res.drawable.desert)),
                        actual = stateList.find { it.first == SaveCellularTrafficCondition }?.second
                    )
                }
            }
        }
    }
}