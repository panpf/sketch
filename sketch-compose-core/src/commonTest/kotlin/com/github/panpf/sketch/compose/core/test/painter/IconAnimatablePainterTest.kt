package com.github.panpf.sketch.compose.core.test.painter

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.compose.painter.rememberIconAnimatablePainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

// TODO Looking forward to Compose Multiplatform supporting ColorResource

class IconAnimatablePainterTest {

    @Composable
    @OptIn(ExperimentalResourceApi::class)
    fun CreateFunctionTest() {
        val painterIcon = SizeColorPainter(Color.Cyan, Size(100f, 100f))
        val resourceIcon = DrawableResource("testIcon")
        val colorBackground = Color.DarkGray
        val resourceBackground = DrawableResource("testBackground")
        val iconSize = Size(200f, 200f)
        val iconTint = Color.Magenta

        rememberIconAnimatablePainter(
            icon = painterIcon,
            background = colorBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )
        rememberIconAnimatablePainter(
            icon = painterIcon,
            background = resourceBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconAnimatablePainter(
            icon = painterIcon,
            background = colorBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = painterIcon,
            background = colorBackground,
            iconTint = iconTint
        )
        rememberIconAnimatablePainter(
            icon = painterIcon,
            background = resourceBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = painterIcon,
            background = resourceBackground,
            iconTint = iconTint
        )
        rememberIconAnimatablePainter(
            icon = painterIcon,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconAnimatablePainter(
            icon = painterIcon,
            background = colorBackground,
        )
        rememberIconAnimatablePainter(
            icon = painterIcon,
            background = resourceBackground,
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


        rememberIconAnimatablePainter(
            icon = resourceIcon,
            background = colorBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )
        rememberIconAnimatablePainter(
            icon = resourceIcon,
            background = resourceBackground,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconAnimatablePainter(
            icon = resourceIcon,
            background = colorBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = resourceIcon,
            background = colorBackground,
            iconTint = iconTint
        )
        rememberIconAnimatablePainter(
            icon = resourceIcon,
            background = resourceBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = resourceIcon,
            background = resourceBackground,
            iconTint = iconTint
        )
        rememberIconAnimatablePainter(
            icon = resourceIcon,
            iconSize = iconSize,
            iconTint = iconTint
        )

        rememberIconAnimatablePainter(
            icon = resourceIcon,
            background = colorBackground,
        )
        rememberIconAnimatablePainter(
            icon = resourceIcon,
            background = resourceBackground,
        )
        rememberIconAnimatablePainter(
            icon = resourceIcon,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainter(
            icon = resourceIcon,
            iconTint = iconTint
        )

        rememberIconAnimatablePainter(
            icon = resourceIcon,
        )
    }
}