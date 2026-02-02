package com.github.panpf.sketch.compose.resources.common.test.state

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.images.Res
import com.github.panpf.sketch.images.moon
import com.github.panpf.sketch.painter.equitablePainterResource
import com.github.panpf.sketch.state.PainterStateImage
import com.github.panpf.sketch.state.rememberPainterStateImage
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class PainterStateImageComposeResourcesTest {

    @Test
    fun testRememberPainterStateImage() {
        runComposeUiTest {
            setContent {
                assertEquals(
                    expected = PainterStateImage(equitablePainterResource(Res.drawable.moon)),
                    actual = rememberPainterStateImage(Res.drawable.moon)
                )
            }
        }
    }
}