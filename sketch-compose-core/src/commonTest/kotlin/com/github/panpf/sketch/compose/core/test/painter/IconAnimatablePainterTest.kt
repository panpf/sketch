package com.github.panpf.sketch.compose.core.test.painter

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.painter.asEquality
import com.github.panpf.sketch.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.test.utils.SizeColorPainter

class IconAnimatablePainterTest {
    // TODO test

    @Composable
    fun CreateFunctionTest() {
        val painterIcon = Color.Cyan.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val painterBackground =
            Color.Gray.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val colorBackground = Color.DarkGray
        val iconSize = Size(200f, 200f)
        val iconTint = Color.Magenta

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