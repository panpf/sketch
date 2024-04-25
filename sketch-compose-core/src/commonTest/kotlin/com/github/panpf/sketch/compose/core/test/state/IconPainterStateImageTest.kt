package com.github.panpf.sketch.compose.core.test.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.util.asEquality
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

class IconPainterStateImageTest {
    // TODO test

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun CreateFunctionTest() {
        val painterIcon =
            Color.Cyan.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val resourceIcon = DrawableResource("testIcon")
        val painterBackground =
            Color.Gray.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val colorBackground = Color.DarkGray
        val resourceBackground = DrawableResource("testBackground")
        val iconSize = Size(200f, 200f)
        val iconTint = Color.Magenta

        // painter icon
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
            background = resourceBackground,
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
            background = painterBackground,
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

        // resource icon
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
            background = painterBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
            iconSize = iconSize,
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = resourceBackground,
            iconSize = iconSize,
        )

        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = painterBackground,
            iconTint = iconTint
        )
        rememberIconPainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
            iconTint = iconTint
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
            background = painterBackground,
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