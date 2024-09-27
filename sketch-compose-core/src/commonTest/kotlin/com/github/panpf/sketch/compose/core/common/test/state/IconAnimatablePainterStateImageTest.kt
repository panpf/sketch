@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.state.rememberIconAnimatablePainterStateImage
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.asAnimatable
import kotlin.test.Test

class IconAnimatablePainterStateImageTest {
    // TODO test

    @Test
    fun testRememberIconAnimatablePainterStateImage() {
        runComposeUiTest {
            val painterIcon =
                Color.Cyan.let {
                    SizeColorPainter(it, Size(100f, 100f)).asAnimatable().asEquitable(it)
                }
            val painterBackground =
                Color.Gray.let { ColorPainter(it).asEquitable(it) }
            val colorBackground = Color.DarkGray
            val iconSize = Size(200f, 200f)
            val iconTint = Color.Magenta
            setContent {
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
    }
}