package com.github.panpf.sketch.compose.core.test.painter

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.compose.state.rememberIconPainterStateImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

class IconPainterStateImageTest {

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
            background = resourceBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconPainterStateImage(
            icon = painterIcon,
            background = colorBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = painterIcon,
            background = colorBackground,
            iconTint = iconTint
        )
        rememberIconPainterStateImage(
            icon = painterIcon,
            background = resourceBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = painterIcon,
            background = resourceBackground,
            iconTint = iconTint
        )
        rememberIconPainterStateImage(
            icon = painterIcon,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconPainterStateImage(
            icon = painterIcon,
            background = colorBackground,
        )
        rememberIconPainterStateImage(
            icon = painterIcon,
            background = resourceBackground,
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


        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = painterBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = resourceBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
            iconTint = iconTint
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = resourceBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = resourceBackground,
            iconTint = iconTint
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = resourceBackground,
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            iconTint = iconTint
        )

        rememberIconPainterStateImage(
            icon = resourceIcon,
        )
    }
}