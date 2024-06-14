package com.github.panpf.sketch.compose.resources.test.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.painter.asEquality
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.test.utils.SizeColorPainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi

class IconPainterStateImageComposeResourcesTest {
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

        rememberIconPainterStateImage(
            icon = painterIcon,
            background = resourceBackground,
            iconSize = iconSize,
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
            background = resourceBackground,
        )
    }
}