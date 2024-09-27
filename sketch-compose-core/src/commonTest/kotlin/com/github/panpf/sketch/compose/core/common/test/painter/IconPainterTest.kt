@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.test.utils.SizeColorPainter
import kotlin.test.Test

class IconPainterTest {
    // TODO test

    @Test
    fun testRememberIconPainter() {
        runComposeUiTest {
            val painterIcon =
                Color.Cyan.let { SizeColorPainter(it, Size(100f, 100f)).asEquitable(it) }
            val painterBackground =
                Color.Gray.let { ColorPainter(it).asEquitable(it) }
            val colorBackground = Color.DarkGray
            val iconSize = Size(200f, 200f)
            val iconTint = Color.Magenta
            setContent {
                rememberIconPainter(
                    icon = painterIcon,
                    background = painterBackground,
                    iconSize = iconSize,
                    iconTint = iconTint
                )
                rememberIconPainter(
                    icon = painterIcon,
                    background = colorBackground,
                    iconSize = iconSize,
                    iconTint = iconTint
                )

                rememberIconPainter(
                    icon = painterIcon,
                    background = painterBackground,
                    iconSize = iconSize,
                )
                rememberIconPainter(
                    icon = painterIcon,
                    background = colorBackground,
                    iconSize = iconSize,
                )

                rememberIconPainter(
                    icon = painterIcon,
                    background = painterBackground,
                    iconTint = iconTint
                )
                rememberIconPainter(
                    icon = painterIcon,
                    background = colorBackground,
                    iconTint = iconTint
                )

                rememberIconPainter(
                    icon = painterIcon,
                    iconSize = iconSize,
                    iconTint = iconTint
                )

                rememberIconPainter(
                    icon = painterIcon,
                    background = painterBackground,
                )
                rememberIconPainter(
                    icon = painterIcon,
                    background = colorBackground,
                )

                rememberIconPainter(
                    icon = painterIcon,
                    iconSize = iconSize,
                )
                rememberIconPainter(
                    icon = painterIcon,
                    iconTint = iconTint
                )

                rememberIconPainter(
                    icon = painterIcon,
                )
            }
        }
    }
}