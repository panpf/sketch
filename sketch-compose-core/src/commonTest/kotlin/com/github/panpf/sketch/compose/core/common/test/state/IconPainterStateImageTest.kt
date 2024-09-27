@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.test.utils.SizeColorPainter
import kotlin.test.Test

class IconPainterStateImageTest {
    // TODO test

    @Test
    fun testRememberIconPainterStateImage() {
        runComposeUiTest {
            val painterIcon =
                Color.Cyan.let { SizeColorPainter(it, Size(100f, 100f)).asEquitable(it) }
            val painterBackground =
                Color.Gray.let { ColorPainter(it).asEquitable(it) }
            val colorBackground = Color.DarkGray
            val iconSize = Size(200f, 200f)
            val iconTint = Color.Magenta
            setContent {
                rememberIconPainterStateImage(
                    icon = painterIcon,
                    background = painterBackground,
                    iconSize = iconSize,
                    iconTint = iconTint
                )
                rememberIconPainterStateImage(
                    icon = painterIcon,
                    background = colorBackground,
                    iconSize = iconSize,
                    iconTint = iconTint
                )

                rememberIconPainterStateImage(
                    icon = painterIcon,
                    background = painterBackground,
                    iconSize = iconSize,
                )
                rememberIconPainterStateImage(
                    icon = painterIcon,
                    background = colorBackground,
                    iconSize = iconSize,
                )

                rememberIconPainterStateImage(
                    icon = painterIcon,
                    background = painterBackground,
                    iconTint = iconTint
                )
                rememberIconPainterStateImage(
                    icon = painterIcon,
                    background = colorBackground,
                    iconTint = iconTint
                )

                rememberIconPainterStateImage(
                    icon = painterIcon,
                    iconSize = iconSize,
                    iconTint = iconTint
                )

                rememberIconPainterStateImage(
                    icon = painterIcon,
                    background = painterBackground,
                )
                rememberIconPainterStateImage(
                    icon = painterIcon,
                    background = colorBackground,
                )

                rememberIconPainterStateImage(
                    icon = painterIcon,
                    iconSize = iconSize,
                )
                rememberIconPainterStateImage(
                    icon = painterIcon,
                    iconTint = iconTint
                )

                rememberIconPainterStateImage(
                    icon = painterIcon,
                )
            }
        }
    }
}