package com.github.panpf.sketch.compose.core.test.painter

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.painter.asEquality
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

class IconPainterTest {
    // TODO test

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun CreateFunctionTest() {
        val painterIcon = Color.Cyan.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val resourceIcon = DrawableResource("testIcon")
        val painterBackground = Color.Gray.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val colorBackground = Color.DarkGray
        val resourceBackground = DrawableResource("testBackground")
        val iconSize = Size(200f, 200f)
        val iconTint = Color.Magenta

        // painter icon
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
            background = resourceBackground,
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
            background = resourceBackground,
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
            background = resourceBackground,
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
            background = resourceBackground,
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

        // resource icon
        rememberIconPainter(
            icon = resourceIcon,
            background = painterBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )
        rememberIconPainter(
            icon = resourceIcon,
            background = colorBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )
        rememberIconPainter(
            icon = resourceIcon,
            background = resourceBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconPainter(
            icon = resourceIcon,
            background = painterBackground,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = resourceIcon,
            background = colorBackground,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = resourceIcon,
            background = resourceBackground,
            iconSize = iconSize,
        )

        rememberIconPainter(
            icon = resourceIcon,
            background = painterBackground,
            iconTint = iconTint
        )
        rememberIconPainter(
            icon = resourceIcon,
            background = colorBackground,
            iconTint = iconTint
        )
        rememberIconPainter(
            icon = resourceIcon,
            background = resourceBackground,
            iconTint = iconTint
        )

        rememberIconPainter(
            icon = resourceIcon,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconPainter(
            icon = resourceIcon,
            background = painterBackground,
        )
        rememberIconPainter(
            icon = resourceIcon,
            background = colorBackground,
        )
        rememberIconPainter(
            icon = resourceIcon,
            background = resourceBackground,
        )

        rememberIconPainter(
            icon = resourceIcon,
            iconSize = iconSize,
        )
        rememberIconPainter(
            icon = resourceIcon,
            iconTint = iconTint
        )

        rememberIconPainter(
            icon = resourceIcon,
        )
    }
}