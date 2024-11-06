package com.github.panpf.sketch.compose.resources.common.test.painter

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.equitablePainterResource
import com.github.panpf.sketch.painter.rememberEquitablePainterResource
import com.github.panpf.sketch.test.compose.resources.Res
import com.github.panpf.sketch.test.compose.resources.moon
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.current
import org.jetbrains.compose.resources.painterResource
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class EquitablePainterComposeResourcesTest {

    @Test
    fun testRememberEquitablePainterResource() {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return
        }
        runComposeUiTest {
            setContent {
                assertEquals(
                    expected = painterResource(Res.drawable.moon).asEquitable(equalityKey = Res.drawable.moon),
                    actual = rememberEquitablePainterResource(Res.drawable.moon)
                )
            }
        }
    }

    @Test
    fun testEquitablePainterResource() {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return
        }
        runComposeUiTest {
            setContent {
                assertEquals(
                    expected = painterResource(Res.drawable.moon).asEquitable(equalityKey = Res.drawable.moon),
                    actual = equitablePainterResource(Res.drawable.moon)
                )
            }
        }
    }
}