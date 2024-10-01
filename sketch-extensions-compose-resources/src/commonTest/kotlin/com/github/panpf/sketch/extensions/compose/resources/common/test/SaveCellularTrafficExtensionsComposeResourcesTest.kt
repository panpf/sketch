package com.github.panpf.sketch.extensions.compose.resources.common.test

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.rememberEquitablePainterResource
import com.github.panpf.sketch.state.ComposableErrorStateImage
import com.github.panpf.sketch.state.PainterStateImage
import com.github.panpf.sketch.state.SaveCellularTrafficCondition
import com.github.panpf.sketch.state.saveCellularTrafficError
import com.github.panpf.sketch.test.utils.compose.core.resources.Res
import com.github.panpf.sketch.test.utils.compose.core.resources.sample
import org.jetbrains.compose.resources.DrawableResource
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SaveCellularTrafficExtensionsComposeResourcesTest {

    @Test
    fun testSaveCellularTrafficError() {
        runComposeUiTest {
            setContent {
                ComposableErrorStateImage(null as DrawableResource?) {
                    saveCellularTrafficError(Res.drawable.sample)
                }.apply {
                    assertEquals(
                        expected = PainterStateImage(rememberEquitablePainterResource(Res.drawable.sample)),
                        actual = stateList.find { it.first == SaveCellularTrafficCondition }!!.second
                    )
                }
            }
        }
    }
}