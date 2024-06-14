package com.github.panpf.sketch.compose.core.test.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.painter.asEquality
import com.github.panpf.sketch.state.rememberIconAnimatablePainterStateImage
import com.github.panpf.sketch.test.utils.SizeColorPainter

class IconAnimatablePainterStateImageCommonTest {
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

        // painter icon
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = painterBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = colorBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = painterBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = colorBackground,
            iconSize = iconSize,
        )

        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = painterBackground,
            iconTint = iconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = colorBackground,
            iconTint = iconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = painterBackground,
        )
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = colorBackground,
        )

        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            iconTint = iconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
        )
    }
}