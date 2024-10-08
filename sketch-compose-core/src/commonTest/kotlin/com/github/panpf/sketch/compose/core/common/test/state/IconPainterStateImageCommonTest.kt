package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.painter.asEquality
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.test.utils.SizeColorPainter

class IconPainterStateImageCommonTest {
    // TODO test

    @Composable
    fun CreateFunctionTest() {
        val painterIcon =
            Color.Cyan.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val painterBackground =
            Color.Gray.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val colorBackground = Color.DarkGray
        val iconSize = Size(200f, 200f)
        val iconTint = Color.Magenta

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