package com.github.panpf.sketch.compose.resources.common.test.state

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.equitablePainterResource
import com.github.panpf.sketch.state.PainterStateImage
import com.github.panpf.sketch.state.rememberPainterStateImage
import com.github.panpf.sketch.test.compose.resources.Res
import com.github.panpf.sketch.test.compose.resources.moon
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.current
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class PainterStateImageComposeResourcesTest {

    @Test
    fun testRememberPainterStateImage() {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return
        }
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