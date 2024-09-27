@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.asAnimatable
import kotlin.test.Test

class IconAnimatablePainterTest {
    // TODO test

    @Test
    fun testRememberIconAnimatablePainter() {
        runComposeUiTest {
            val painterIcon =
                Color.Cyan.let {
                    SizeColorPainter(it, Size(100f, 100f)).asAnimatable().asEquitable(it)
                }
            val painterBackground = Color.Gray.let { ColorPainter(it).asEquitable(it) }
            val colorBackground = Color.DarkGray
            val iconSize = Size(200f, 200f)
            val iconTint = Color.Magenta
            setContent {
                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    background = painterBackground,
                    iconSize = iconSize,
                    iconTint = iconTint
                )
                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    background = colorBackground,
                    iconSize = iconSize,
                    iconTint = iconTint
                )

                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    background = painterBackground,
                    iconSize = iconSize,
                )
                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    background = colorBackground,
                    iconSize = iconSize,
                )

                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    background = painterBackground,
                    iconTint = iconTint
                )
                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    background = colorBackground,
                    iconTint = iconTint
                )

                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    iconSize = iconSize,
                    iconTint = iconTint
                )

                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    background = painterBackground,
                )
                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    background = colorBackground,
                )

                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    iconSize = iconSize,
                )
                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    iconTint = iconTint
                )

                rememberIconAnimatablePainter(
                    icon = painterIcon,
                )
            }
        }
    }
}