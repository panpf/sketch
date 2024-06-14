package com.github.panpf.sketch.compose.core.test.painter

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.painter.asEquality
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.test.utils.SizeColorPainter

class IconPainterTest {
    // TODO test

    @Composable
    fun CreateFunctionTest() {
        val painterIcon = Color.Cyan.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val painterBackground =
            Color.Gray.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val colorBackground = Color.DarkGray
        val iconSize = Size(200f, 200f)
        val iconTint = Color.Magenta

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