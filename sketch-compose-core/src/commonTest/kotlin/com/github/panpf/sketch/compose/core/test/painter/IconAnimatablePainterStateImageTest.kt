package com.github.panpf.sketch.compose.core.test.painter

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.compose.state.rememberIconAnimatablePainterStateImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

class IconAnimatablePainterStateImageTest {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun CreateFunctionTest() {
        val painterIcon = SizeColorPainter(Color.Cyan, Size(100f, 100f))
        val resourceIcon = DrawableResource("testIcon")
        val painterBackground = SizeColorPainter(Color.Gray, Size(1000f, 1000f))
        val colorBackground = Color.DarkGray
        val resourceBackground = DrawableResource("testBackground")
        val iconSize = Size(200f, 200f)
        val iconTint = Color.Magenta

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
            background = resourceBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = colorBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = colorBackground,
            iconTint = iconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = resourceBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = resourceBackground,
            iconTint = iconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = colorBackground,
        )
        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = resourceBackground,
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


        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = painterBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = resourceBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
            iconTint = iconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = resourceBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = resourceBackground,
            iconTint = iconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = resourceBackground,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            iconTint = iconTint
        )

        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
        )
    }
}