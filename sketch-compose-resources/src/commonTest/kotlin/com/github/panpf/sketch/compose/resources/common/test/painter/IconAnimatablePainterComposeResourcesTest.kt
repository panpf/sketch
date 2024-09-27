@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.resources.common.test.painter

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.test.utils.SizeColorPainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi

class IconAnimatablePainterComposeResourcesTest {
    // TODO test

    //    @Test
    @Composable
    @OptIn(InternalResourceApi::class)
    fun testRememberIconAnimatablePainter() {
        runComposeUiTest {
            val painterIcon =
                Color.Cyan.let { SizeColorPainter(it, Size(100f, 100f)).asEquitable(it) }
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
                Color.Gray.let { SizeColorPainter(it, Size(100f, 100f)).asEquitable(it) }
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
            setContent {
                rememberIconAnimatablePainter(
                    icon = resourceIcon,
                    background = painterBackground,
                    iconSize = iconSize,
                    iconTint = iconTint
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
                    background = painterBackground,
                    iconSize = iconSize,
                )
                rememberIconAnimatablePainter(
                    icon = resourceIcon,
                    background = colorBackground,
                    iconSize = iconSize,
                )
                rememberIconAnimatablePainter(
                    icon = resourceIcon,
                    background = resourceBackground,
                    iconSize = iconSize,
                )

                rememberIconAnimatablePainter(
                    icon = resourceIcon,
                    background = painterBackground,
                    iconTint = iconTint
                )
                rememberIconAnimatablePainter(
                    icon = resourceIcon,
                    background = colorBackground,
                    iconTint = iconTint
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
                    background = painterBackground,
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

                rememberIconAnimatablePainter(
                    icon = painterIcon,
                    background = resourceBackground,
                    iconSize = iconSize,
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
                    background = resourceBackground,
                )
            }
        }
    }
}