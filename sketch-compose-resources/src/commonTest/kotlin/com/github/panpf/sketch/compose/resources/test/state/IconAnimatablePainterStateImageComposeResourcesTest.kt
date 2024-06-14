package com.github.panpf.sketch.compose.resources.test.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.painter.asEquality
import com.github.panpf.sketch.state.rememberIconAnimatablePainterStateImage
import com.github.panpf.sketch.test.utils.SizeColorPainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi

class IconAnimatablePainterStateImageComposeResourcesTest {
    // TODO test

    @OptIn(InternalResourceApi::class)
    @Composable
    fun CreateFunctionTest() {
        val painterIcon =
            Color.Cyan.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val resourceIcon = DrawableResource(
            "drawable:test_icon",
            setOf(
                org.jetbrains.compose.resources.ResourceItem(
                    setOf(),
                    "composeResources/sketch_root.sample.generated.resources/drawable/ic_info_baseline.xml",
                    -1,
                    -1
                ),
            )
        )
        val painterBackground =
            Color.Gray.let { SizeColorPainter(it, Size(100f, 100f)).asEquality(it) }
        val colorBackground = Color.DarkGray
        val resourceBackground = DrawableResource(
            "drawable:test_background",
            setOf(
                org.jetbrains.compose.resources.ResourceItem(
                    setOf(),
                    "composeResources/sketch_root.sample.generated.resources/drawable/ic_info_baseline.xml",
                    -1,
                    -1
                ),
            )
        )
        val iconSize = Size(200f, 200f)
        val iconTint = Color.Magenta

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
            background = painterBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
            iconSize = iconSize,
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = resourceBackground,
            iconSize = iconSize,
        )

        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = painterBackground,
            iconTint = iconTint
        )
        rememberIconAnimatablePainterStateImage(
            icon = resourceIcon,
            background = colorBackground,
            iconTint = iconTint
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
            background = painterBackground,
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

        rememberIconAnimatablePainterStateImage(
            icon = painterIcon,
            background = resourceBackground,
            iconSize = iconSize,
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
            background = resourceBackground,
        )
    }
}